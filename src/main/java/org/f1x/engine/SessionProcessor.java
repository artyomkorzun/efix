package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionSettings;
import org.f1x.connector.Connector;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.FixTags;
import org.f1x.message.parser.MessageParser;
import org.f1x.schedule.SessionSchedule;
import org.f1x.state.SessionState;
import org.f1x.state.SessionStatus;
import org.f1x.store.MessageStore;
import org.f1x.util.ByteSequence;
import org.f1x.util.EpochClock;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.concurrent.IdleStrategy;
import org.f1x.util.concurrent.MessageHandler;
import org.f1x.util.concurrent.RingBuffer;
import org.f1x.util.concurrent.Worker;

import static org.f1x.engine.SessionUtil.*;
import static org.f1x.state.SessionStatus.*;

public class SessionProcessor implements Worker {

    protected final SessionSettings settings;
    protected final EpochClock clock;
    protected final SessionState state;
    protected final MessageStore store;
    protected final MessageLog log;
    protected final SessionSchedule schedule;

    protected final Connector connector;
    protected final RingBuffer messageQueue;
    protected final IdleStrategy idleStrategy;
    protected final MessageParser parser;
    protected final MessageBuilder builder;
    protected final MutableBuffer buffer;
    protected final Receiver receiver;
    protected final Sender sender;
    protected final MessagePacker packer;
    protected final Resender resender = createResender();

    protected final Header header = new Header();
    protected final Logon logon = new Logon();
    protected final TestRequest testRequest = new TestRequest();
    protected final ResendRequest resendRequest = new ResendRequest();
    protected final SequenceReset sequenceReset = new SequenceReset();
    protected final ByteSequence outMsgType = new ByteSequence();

    protected final MessageHandler inMessageHandler = createInMessageHandler();
    protected final MessageHandler outMessageHandler = createOutMessageHandler();

    public SessionProcessor(SessionSettings settings, EpochClock clock, SessionSchedule schedule,
                            SessionState state, MessageStore store, MessageLog log,
                            Connector connector, RingBuffer messageQueue, IdleStrategy idleStrategy,
                            MessageParser parser, MessageBuilder builder, MutableBuffer buffer,
                            Receiver receiver, Sender sender, MessagePacker packer) {
        this.settings = settings;
        this.clock = clock;
        this.state = state;
        this.store = store;
        this.log = log;
        this.schedule = schedule;
        this.connector = connector;
        this.messageQueue = messageQueue;
        this.idleStrategy = idleStrategy;
        this.parser = parser;
        this.builder = builder;
        this.buffer = buffer;
        this.receiver = receiver;
        this.sender = sender;
        this.packer = packer;
    }

    @Override
    public void onStart() {
        // TODO: catch exceptions
        state.open();
        store.open();
        log.open();
        connector.open();
    }

    @Override
    public void onClose() {
        state.close();
        store.close();
        log.close();
        connector.close();
    }

    @Override
    public void doWork() {
        int work = work();
        if (work == 0)
            flush();

        idleStrategy.idle(work);
    }

    protected int work() {
        int work = 0;

        work += checkSession(clock.time());
        work += processInboundMessages();
        work += processOutboundMessages();
        work += processTimers(clock.time());

        return work;
    }

    protected void flush() {
        state.flush();
        store.flush();
        log.flush();
    }

    protected int checkSession(long now) {
        int work = 0;
        try {
            if (state.getStatus() == DISCONNECTED)
                work += checkSessionStart(now);
            else
                work += checkSessionEnd(now);
        } catch (Throwable e) {
            onError(e);
            work += 1;
        }

        return work;
    }

    protected int processInboundMessages() {
        if (state.getStatus() == DISCONNECTED)
            return 0;

        try {
            int bytesRead = receiver.receive(inMessageHandler);
            if (bytesRead == -1) {
                disconnect("No more data");
                return 1;
            }

            return bytesRead;
        } catch (Throwable e) {
            onError(e);
            return 1;
        }
    }

    protected int processOutboundMessages() {
        try {
            return messageQueue.read(outMessageHandler);
        } catch (Throwable e) {
            onError(e);
            return 1;
        }
    }

    protected int processTimers(long now) {
        int work = 0;
        SessionStatus status = state.getStatus();
        if (status == SOCKET_CONNECTED || status == LOGON_SENT) {
            work += checkLogonTimeout(now);
        } else if (status == APPLICATION_CONNECTED) {
            work += checkInHeartbeatTimeout(now);
            work += checkOutHeartbeatTimeout(now);
        } else if (status == LOGOUT_SENT) {
            work += checkLogoutTimeout(now);
        }

        return work;
    }

    protected int checkSessionStart(long now) {
        int work = 0;
        long start = schedule.getStartTime(now);
        if (now >= start) {
            boolean connected = connect();
            if (connected) {
                if (state.getSessionStartTime() < start) {
                    state.setNextTargetSeqNum(1);
                    state.setNextSenderSeqNum(1);
                    store.clear();
                }

                state.setSessionStartTime(now);

                if (settings.isInitiator())
                    sendLogon(settings.resetSeqNumsOnEachLogon());

                work += 1;
            }
        } else if (connector.isConnectionPending()) {
            disconnect("Session expired");
            work += 1;
        }

        return work;
    }

    protected int checkSessionEnd(long now) {
        int work = 0;
        long end = schedule.getEndTime(state.getSessionStartTime());
        if (now >= end) {
            SessionStatus status = state.getStatus();
            if (status == SOCKET_CONNECTED || status == LOGON_SENT) {
                sendLogout("Session expired");
                disconnect("Session expired");
                work += 1;
            } else if (status == APPLICATION_CONNECTED) {
                sendLogout("Session expired");
                work += 1;
            }
        }

        return work;
    }

    protected int checkLogonTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getSessionStartTime();
        int timeout = settings.getLogonTimeout();
        if (elapsed >= timeout) {
            sendLogout("Logon timeout");
            disconnect("Logon timeout");
            work += 1;
        }

        return work;
    }

    protected int checkLogoutTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastSentTime();
        int timeout = settings.getLogoutTimeout();
        if (elapsed >= timeout) {
            sendLogout("Logout timeout");
            disconnect("Logout timeout");
            work += 1;
        }

        return work;
    }

    protected int checkInHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastReceivedTime();
        int timeout = settings.getHeartbeatTimeout();
        if (elapsed >= 2 * timeout) {
            sendLogout("Heartbeat timeout");
            disconnect("Heartbeat timeout");
            work += 1;
        } else if (elapsed >= timeout && !state.isTestRequestSent()) {
            sendTestRequest("Heartbeat timeout");
            work += 1;
        }

        return work;
    }

    protected int checkOutHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastSentTime();
        int timeout = settings.getHeartbeatTimeout();
        if (elapsed >= timeout) {
            sendHeartbeat(null);
            work += 1;
        }

        return work;
    }

    protected boolean connect() {
        Channel channel = connector.connect();
        if (channel != null) {
            receiver.setChannel(channel);
            sender.setChannel(channel);
            setStatus(SOCKET_CONNECTED);
        }

        return channel != null;
    }

    protected void disconnect(CharSequence cause) {
        SessionStatus status = state.getStatus();
        if (status != DISCONNECTED && status != SOCKET_CONNECTED)
            setStatus(SOCKET_CONNECTED);

        if (connector.isConnected() || connector.isConnectionPending()) {
            connector.disconnect();
            receiver.setChannel(null);
            sender.setChannel(null);
        }

        if (status != DISCONNECTED)
            setStatus(DISCONNECTED);
    }

    protected void processMessage(Buffer buffer, int offset, int length) {
        long time = clock.time();
        state.setLastReceivedTime(time);
        log.log(true, time, buffer, offset, length);

        parser.wrap(buffer, offset, length);
        parseHeader(parser, FIXVersion.FIX44, header);
        validateHeader(header);
        parser.reset();

        if (AdminMessageTypes.isAdmin(header.getMsgType()))
            processAdminMessage(header, parser);
        else
            processAppMessage(header, parser);
    }

    protected void processAdminMessage(Header header, MessageParser parser) {
        switch (header.getMsgType().charAt(0)) {
            case AdminMessageTypes.LOGON:
                processLogon(header, parser);
                break;
            case AdminMessageTypes.HEARTBEAT:
                processHeartbeat(header, parser);
                break;
            case AdminMessageTypes.TEST:
                processTestRequest(header, parser);
                break;
            case AdminMessageTypes.RESEND:
                processResendRequest(header, parser);
                break;
            case AdminMessageTypes.REJECT:
                processReject(header, parser);
                break;
            case AdminMessageTypes.RESET:
                processSequenceReset(header, parser);
                break;
            case AdminMessageTypes.LOGOUT:
                processLogout(header, parser);
                break;
        }
    }

    protected void processLogon(Header header, MessageParser parser) {
        assertStatus(SOCKET_CONNECTED, LOGON_SENT, APPLICATION_CONNECTED);
        assertNotDuplicate(header.possDup(), "Logon with PossDup(44)=Y");

        Logon logon = parseLogon(parser, this.logon);
        boolean resetSeqNums = logon.resetSeqNums();
        if (resetSeqNums)
            state.setNextTargetSeqNum(1);

        state.setSeqNumsSynchronized(false);
        int msgSeqNum = header.msgSeqNum();
        boolean expectedSeqNum = checkTargetSeqNum(msgSeqNum, resetSeqNums);
        if (expectedSeqNum)
            state.setNextTargetSeqNum(msgSeqNum + 1);

        validateLogon(logon);
        onAdminMessage(header, parser.reset());

        SessionStatus status = state.getStatus();
        setStatus(LOGON_RECEIVED);
        if (status == SOCKET_CONNECTED || status == APPLICATION_CONNECTED)
            sendLogon(resetSeqNums);

        if (!expectedSeqNum)
            sendResendRequest(state.getNextTargetSeqNum(), 0);

        sendTestRequest("MsgSeqNum check");
        setStatus(APPLICATION_CONNECTED);
    }

    protected void processHeartbeat(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Heartbeat with PossDup(44)=Y");

        state.setTestRequestSent(false);
        int msgSeqNum = header.msgSeqNum();
        boolean expectedSeqNum = checkTargetSeqNum(msgSeqNum, state.isSeqNumsSynchronized());
        if (expectedSeqNum) {
            state.setNextTargetSeqNum(msgSeqNum + 1);
            state.setSeqNumsSynchronized(true);
        }

        onAdminMessage(header, parser);
        if (!expectedSeqNum)
            sendResendRequest(state.getNextTargetSeqNum(), 0);
    }

    protected void processTestRequest(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "TestRequest with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.isSeqNumsSynchronized()))
            state.setNextTargetSeqNum(msgSeqNum + 1);

        TestRequest request = parseTestRequest(parser, testRequest);
        validateTestRequest(request);

        onAdminMessage(header, parser.reset());
        if (state.getStatus() == APPLICATION_CONNECTED)
            sendHeartbeat(request.testReqID());
    }

    protected void processResendRequest(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "ResendRequest with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.isSeqNumsSynchronized()))
            state.setNextTargetSeqNum(msgSeqNum + 1);

        ResendRequest request = parseResendRequest(parser, resendRequest);
        validateResendRequest(request);
        onAdminMessage(header, parser.reset());
        resendMessages(request.beginSeqNo(), request.endSeqNo() == 0 ? (state.getNextSenderSeqNum() - 1) : request.endSeqNo());
    }

    protected void processReject(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        if (header.possDup())
            state.setSeqNumsSynchronized(true);

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.isSeqNumsSynchronized())) {
            state.setNextTargetSeqNum(msgSeqNum + 1);
            onAdminMessage(header, parser);
        }
    }

    protected void processSequenceReset(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Sequence Reset with PossDup(44)=Y");

        SequenceReset reset = parseSequenceReset(parser, sequenceReset);
        if (reset.isGapFill())
            checkTargetSeqNum(header.msgSeqNum(), true);

        validateSequenceReset(reset);
        onAdminMessage(header, parser.reset());
        state.setSeqNumsSynchronized(true);
        state.setNextTargetSeqNum(reset.newSeqNo());
    }

    protected void processLogout(Header header, MessageParser parser) {
        assertStatus(LOGON_SENT, APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Logout with PossDup(44)=Y");

        boolean expectedSeqNum = checkTargetSeqNum(header.msgSeqNum(), state.isSeqNumsSynchronized());
        if (expectedSeqNum)
            state.setNextTargetSeqNum(header.msgSeqNum() + 1);

        onAdminMessage(header, parser);

        SessionStatus status = state.getStatus();
        setStatus(LOGOUT_RECEIVED);
        if (status == APPLICATION_CONNECTED)
            sendLogout("Responding to Logout");

        disconnect("Logout");
    }

    protected void processAppMessage(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        if (header.possDup())
            state.setSeqNumsSynchronized(true);

        if (checkTargetSeqNum(header.msgSeqNum(), state.isSeqNumsSynchronized())) {
            state.setNextTargetSeqNum(header.msgSeqNum() + 1);
            onAppMessage(header, parser);
        }
    }

    protected void validateHeader(Header header) {
    }

    protected void validateLogon(Logon logon) {
        if (state.getStatus() == APPLICATION_CONNECTED && !logon.resetSeqNums())
            throw new FieldException(FixTags.ResetSeqNumFlag, "In-session logon should contain ResetSeqNumFlag(141)=Y");

        SessionUtil.validateLogon(settings.getHeartbeatInterval(), logon);
    }

    protected void validateTestRequest(TestRequest request) {
        SessionUtil.validateTestRequest(request);
    }

    protected void validateResendRequest(ResendRequest request) {
        SessionUtil.validateResendRequest(request);
    }

    protected void validateSequenceReset(SequenceReset reset) {
        SessionUtil.validateSequenceReset(state.getNextTargetSeqNum(), reset);
    }

    protected void sendLogon(boolean resetSeqNums) {
        if (resetSeqNums) {
            state.setNextSenderSeqNum(1);
            store.clear();
        }

        builder.wrap(buffer);
        makeLogon(resetSeqNums, builder);
        sendMessage(true, buffer, 0, builder.length());
        setStatus(LOGON_SENT);
    }

    protected void sendLogout(CharSequence text) {
        builder.wrap(buffer);
        makeLogout(text, builder);
        sendMessage(true, buffer, 0, builder.length());
        setStatus(LOGOUT_SENT);
    }

    protected void sendHeartbeat(CharSequence testReqID) {
        builder.wrap(buffer);
        makeHeartbeat(testReqID, builder);
        sendMessage(true, buffer, 0, builder.length());
    }

    protected void sendTestRequest(CharSequence testReqID) {
        state.setTestRequestSent(true);
        builder.wrap(buffer);
        makeTestRequest(testReqID, builder);
        sendMessage(true, buffer, 0, builder.length());
    }

    protected void sendResendRequest(int beginSeqNo, int endSeqNo) {
        builder.wrap(buffer);
        makeResendRequest(beginSeqNo, endSeqNo, builder);
        sendMessage(true, buffer, 0, builder.length());
    }

    protected void sendReject(Buffer buffer, int offset, int length) {
        SessionStatus status = state.getStatus();
        boolean send = (status == APPLICATION_CONNECTED || status == LOGON_SENT);
        sendMessage(send, buffer, offset, length);
    }

    protected void sendSequenceReset(boolean gapFill, int seqNum, int newSeqNo) {
        builder.wrap(buffer);
        makeSequenceReset(gapFill, newSeqNo, builder);
        sendMessage(true, seqNum, buffer, 0, builder.length());
    }

    protected void sendAppMessage(Buffer buffer, int offset, int length) {
        SessionStatus status = state.getStatus();
        boolean send = (status == APPLICATION_CONNECTED || status == LOGON_SENT);
        sendMessage(send, buffer, offset, length);
    }

    protected void sendMessage(boolean send, Buffer buffer, int offset, int length) {
        int seqNum = state.getNextSenderSeqNum();
        state.setNextSenderSeqNum(seqNum + 1);
        sendMessage(send, seqNum, buffer, offset, length);
    }

    protected void resendMessages(int beginSeqNo, int endSeqNo) {
        resender.resendMessages(beginSeqNo, endSeqNo, store);
    }

    protected void resendMessage(int seqNum, long origSendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        long time = clock.time();
        Buffer message = packer.pack(seqNum, time, origSendingTime, msgType, body, offset, length);
        sendMessage(time, message);
    }

    protected void sendMessage(boolean send, int seqNum, Buffer buffer, int offset, int length) {
        parser.wrap(buffer, offset, length);
        CharSequence msgType = parseMessageType(parser, outMsgType);

        int fieldLength = parser.length();
        length -= fieldLength;
        offset += fieldLength;

        long time = clock.time();
        try {
            if (send) {
                Buffer message = packer.pack(seqNum, time, msgType, buffer, offset, length);
                sendMessage(time, message);
            }
        } finally {
            if (onStoreMessage(seqNum, time, msgType, buffer, offset, length))
                store.write(seqNum, time, msgType, buffer, offset, length);
        }
    }

    protected void sendMessage(long time, Buffer message) {
        state.setLastSentTime(time);
        int length = message.capacity();

        sender.send(message, 0, length);
        log.log(false, time, message, 0, length);
    }

    protected void makeLogon(boolean resetSeqNum, MessageBuilder builder) {
        SessionUtil.makeLogon(resetSeqNum, settings.getHeartbeatInterval(), builder);
        if (settings.isLogonWithNextExpectedSeqNum())
            builder.addInt(FixTags.NextExpectedMsgSeqNum, state.getNextTargetSeqNum());
    }

    protected void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        SessionUtil.makeHeartbeat(testReqID, builder);
    }

    protected void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        SessionUtil.makeTestRequest(testReqID, builder);
    }

    protected void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        SessionUtil.makeResendRequest(beginSeqNo, endSeqNo, builder);
    }

    protected void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        SessionUtil.makeSequenceReset(gapFill, newSeqNo, builder);
    }

    protected void makeLogout(CharSequence text, MessageBuilder builder) {
        SessionUtil.makeLogout(text, builder);
    }

    protected void setStatus(SessionStatus status) {
        SessionStatus current = state.getStatus();
        if (current != status) {
            state.setStatus(status);
            onStatusUpdate(current, status);
        }
    }

    protected void onStatusUpdate(SessionStatus old, SessionStatus fresh) {
    }

    protected void onAdminMessage(Header header, MessageParser parser) {
    }

    protected void onAppMessage(Header header, MessageParser parser) {
    }

    protected boolean onStoreMessage(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        return !AdminMessageTypes.isAdmin(msgType) || msgType.charAt(0) == AdminMessageTypes.REJECT;
    }

    protected boolean onResendMessage(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        return true;
    }

    protected void onError(Throwable e) {
        disconnect("Error occurred");
    }

    protected MessageHandler createInMessageHandler() {
        return (messageType, buffer, offset, length) -> processMessage(buffer, offset, length);
    }

    protected MessageHandler createOutMessageHandler() {
        return (messageType, buffer, offset, length) -> {
            try {
                sendAppMessage(buffer, offset, length);
            } catch (Throwable e) {
                onError(e);
            }
        };
    }

    protected Resender createResender() {
        return new Resender(this);
    }

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2) {
        SessionUtil.assertStatus(expected1, expected2, state.getStatus());
    }

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2, SessionStatus expected3) {
        SessionUtil.assertStatus(expected1, expected2, expected3, state.getStatus());
    }

    protected boolean checkTargetSeqNum(int actual, boolean checkHigher) {
        return SessionUtil.checkTargetSeqNum(state.getNextTargetSeqNum(), actual, checkHigher);
    }

}
