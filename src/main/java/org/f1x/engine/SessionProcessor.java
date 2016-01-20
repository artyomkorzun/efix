package org.f1x.engine;

import org.f1x.SessionComponent;
import org.f1x.connector.Connector;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.field.Tag;
import org.f1x.message.parser.MessageParser;
import org.f1x.schedule.SessionSchedule;
import org.f1x.state.SessionState;
import org.f1x.state.SessionStatus;
import org.f1x.store.MessageStore;
import org.f1x.util.*;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.concurrent.Worker;
import org.f1x.util.concurrent.buffer.MessageHandler;
import org.f1x.util.concurrent.buffer.RingBuffer;
import org.f1x.util.concurrent.strategy.IdleStrategy;

import java.util.ArrayList;

import static org.f1x.engine.SessionUtil.*;
import static org.f1x.message.AdminMessageTypes.isAdmin;
import static org.f1x.state.SessionStatus.*;

public class SessionProcessor implements Worker {

    protected final EpochClock clock;
    protected final SessionState state;
    protected final MessageStore store;
    protected final MessageLog log;
    protected final SessionSchedule schedule;

    protected final RingBuffer messageQueue;
    protected final IdleStrategy idleStrategy;

    protected final MessageParser parser;
    protected final MessageBuilder builder;

    protected final MutableBuffer messageBuffer;
    protected final MutableBuffer sendBuffer;

    protected final Connector connector;
    protected final Receiver receiver;
    protected final Sender sender;
    protected final MessagePacker packer;
    protected final Resender resender;

    protected final Header header = new Header();
    protected final Logon logon = new Logon();
    protected final TestRequest testRequest = new TestRequest();
    protected final ResendRequest resendRequest = new ResendRequest();
    protected final SequenceReset sequenceReset = new SequenceReset();
    protected final ByteSequenceWrapper outMsgType = new ByteSequenceWrapper();

    protected final MessageHandler inMessageHandler = createInMessageHandler();
    protected final MessageHandler outMessageHandler = createOutMessageHandler();

    protected final int heartbeatInterval;
    protected final int heartbeatTimeout;
    protected final int logonTimeout;
    protected final int logoutTimeout;
    protected final boolean initiator;
    protected final boolean resetSeqNumsOnLogon;
    protected final boolean logonWithNextExpectedSeqNum;

    protected final ArrayList<Disposable> openResources = new ArrayList<>();

    public SessionProcessor(SessionContext context) {
        context.conclude();

        this.clock = context.clock();
        this.state = context.state();
        this.store = context.store();
        this.log = context.log();
        this.schedule = context.schedule();

        this.messageQueue = context.messageQueue();
        this.idleStrategy = context.idleStrategy();

        this.parser = context.parser();
        this.builder = context.builder();

        this.messageBuffer = UnsafeBuffer.allocateHeap(context.messageBufferSize());
        this.sendBuffer = UnsafeBuffer.allocateDirect(context.sendBufferSize());

        this.connector = context.connector();
        this.receiver = new Receiver(context.receiveBufferSize());
        this.sender = new Sender();
        this.resender = new Resender(this);
        this.packer = new MessagePacker(context.sessionID(), context.fixVersion(), sendBuffer);

        this.heartbeatInterval = context.heartbeatInterval();
        this.heartbeatTimeout = context.heartbeatTimeout();
        this.logonTimeout = context.logonTimeout();
        this.logoutTimeout = context.logoutTimeout();
        this.initiator = context.initiator();
        this.resetSeqNumsOnLogon = context.resetSeqNumsOnLogon();
        this.logonWithNextExpectedSeqNum = context.logonWithNextExpectedSeqNum();
    }

    @Override
    public void onStart() {
        try {
            open();
        } catch (Exception e) {
            handleError(e);
            throw e;
        }
    }

    protected void open() {
        Disposable[] resources = {state, store, log, connector};
        for (Disposable resource : resources) {
            resource.open();
            openResources.add(resource);
        }
    }

    @Override
    public void onClose() {
        try {
            close();
        } catch (Exception e) {
            handleError(e);
            throw e;
        }
    }

    protected void close() {
        CloseHelper.close(openResources);
    }

    @Override
    public void doWork() {
        int work = work();
        if (work <= 0)
            flush();

        idleStrategy.idle(work);
    }


    protected int work() {
        int work = 0;

        work += checkSession(clock.time());
        work += processInMessages();
        work += processOutMessages();
        work += processTimers(clock.time());

        return work;
    }

    protected void flush() {
        flush(state);
        flush(store);
        flush(log);
    }

    protected void flush(SessionComponent component) {
        try {
            component.flush();
        } catch (Exception e) {
            handleError(e);
        }
    }

    protected int checkSession(long now) {
        int work = 0;

        try {
            if (state.getStatus() == DISCONNECTED)
                work += checkSessionStart(now);
            else
                work += checkSessionEnd(now);
        } catch (Exception e) {
            work += 1;
            handleError(e);
        }

        return work;
    }

    protected int processInMessages() {
        int work = 0;

        if (state.getStatus() != DISCONNECTED) {
            try {
                int bytesRead = receiver.receive(inMessageHandler);
                if (bytesRead == -1) {
                    disconnect("No more data");
                    work += 1;
                } else {
                    work += bytesRead;
                }
            } catch (Exception e) {
                work += 1;
                handleError(e);
            }
        }

        return work;
    }

    protected int processOutMessages() {
        int work = 0;

        try {
            work += messageQueue.read(outMessageHandler);
        } catch (Exception e) {
            work += 1;
            handleError(e);
        }

        return work;
    }

    protected int processTimers(long now) {
        int work = 0;

        try {
            SessionStatus status = state.getStatus();
            if (status == SOCKET_CONNECTED || status == LOGON_SENT) {
                work += checkLogonTimeout(now);
            } else if (status == APPLICATION_CONNECTED) {
                work += checkInHeartbeatTimeout(now);
                work += checkOutHeartbeatTimeout(now);
            } else if (status == LOGOUT_SENT) {
                work += checkLogoutTimeout(now);
            }
        } catch (Exception e) {
            work += 1;
            handleError(e);
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

                if (initiator)
                    sendLogon(resetSeqNumsOnLogon);

                work += 1;
            }
        } else if (connector.isConnectionPending()) {
            connector.disconnect();
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
        if (elapsed >= logonTimeout)
            throw new TimeoutException(String.format("Logon timeout %s ms. Elapsed %s ms", logonTimeout, elapsed));

        return work;
    }

    protected int checkLogoutTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastSentTime();
        if (elapsed >= logoutTimeout)
            throw new TimeoutException(String.format("Logout timeout %s ms. Elapsed %s ms", logoutTimeout, elapsed));

        return work;
    }

    protected int checkInHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastReceivedTime();
        if (elapsed >= 2 * heartbeatTimeout)
            throw new TimeoutException(String.format("Heartbeat timeout %s ms. Elapsed %s ms", heartbeatTimeout, elapsed));

        if (elapsed >= heartbeatTimeout && !state.isTestRequestSent()) {
            sendTestRequest("Heartbeat timeout");
            work += 1;
        }

        return work;
    }

    protected int checkOutHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.getLastSentTime();
        if (elapsed >= heartbeatTimeout) {
            sendHeartbeat(null);
            work += 1;
        }

        return work;
    }

    protected boolean connect() {
        Channel channel = connector.connect();
        boolean connected = channel != null;
        if (connected) {
            receiver.setChannel(channel);
            sender.setChannel(channel);
            updateStatus(SOCKET_CONNECTED);
        }

        return connected;
    }

    protected void disconnect(CharSequence cause) {
        SessionStatus status = state.getStatus();
        if (status != SOCKET_CONNECTED)
            updateStatus(SOCKET_CONNECTED);

        connector.disconnect();
        receiver.setChannel(null);
        sender.setChannel(null);

        updateStatus(DISCONNECTED);
    }

    protected void processMessage(Buffer buffer, int offset, int length) {
        long time = clock.time();
        state.setLastReceivedTime(time);
        log.log(true, time, buffer, offset, length);

        MessageParser parser = this.parser;
        parser.wrap(buffer, offset, length);

        Header header = parseHeader(parser, this.header);
        validateHeader(header);

        parser.reset();

        if (isAdmin(header.msgType()))
            processAdminMessage(header, parser);
        else
            processAppMessage(header, parser);
    }

    protected void processAdminMessage(Header header, MessageParser parser) {
        switch (header.msgType().charAt(0)) {
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
        assertStatus(SOCKET_CONNECTED, LOGON_SENT);
        assertNotDuplicate(header.possDup(), "Logon with PossDup(44)=Y");

        Logon logon = parseLogon(parser, this.logon);
        boolean resetSeqNums = logon.resetSeqNums();
        if (resetSeqNums)
            state.setNextTargetSeqNum(1);

        int msgSeqNum = header.msgSeqNum();
        boolean expectedSeqNum = checkTargetSeqNum(msgSeqNum, resetSeqNums);

        state.setTargetSeqNumSynchronized(false);
        if (expectedSeqNum)
            state.setNextTargetSeqNum(msgSeqNum + 1);

        validateLogon(logon);
        onAdminMessage(header, parser.reset());

        if (updateStatus(LOGON_RECEIVED) == SOCKET_CONNECTED)
            sendLogon(resetSeqNums);

        if (!expectedSeqNum)
            sendResendRequest(state.getNextTargetSeqNum(), 0);

        sendTestRequest("MsgSeqNum check");
        updateStatus(APPLICATION_CONNECTED);
    }

    protected void processHeartbeat(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Heartbeat with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        checkTargetSeqNum(msgSeqNum, true);

        state.setNextTargetSeqNum(msgSeqNum + 1);
        state.setTestRequestSent(false);
        state.setTargetSeqNumSynchronized(true);

        onAdminMessage(header, parser);
    }

    protected void processTestRequest(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "TestRequest with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.isTargetSeqNumSynchronized()))
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
        if (checkTargetSeqNum(msgSeqNum, state.isTargetSeqNumSynchronized()))
            state.setNextTargetSeqNum(msgSeqNum + 1);

        ResendRequest request = parseResendRequest(parser, resendRequest);
        validateResendRequest(request);

        onAdminMessage(header, parser.reset());
        resendMessages(request.beginSeqNo(), request.endSeqNo() == 0 ? (state.getNextSenderSeqNum() - 1) : request.endSeqNo());
    }

    protected void processReject(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.isTargetSeqNumSynchronized())) {
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
        state.setNextTargetSeqNum(reset.newSeqNo());
    }

    protected void processLogout(Header header, MessageParser parser) {
        assertStatus(LOGON_SENT, APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Logout with PossDup(44)=Y");

        boolean expectedSeqNum = checkTargetSeqNum(header.msgSeqNum(), state.isTargetSeqNumSynchronized());
        if (expectedSeqNum)
            state.setNextTargetSeqNum(header.msgSeqNum() + 1);

        onAdminMessage(header, parser);

        if (updateStatus(LOGOUT_RECEIVED) == APPLICATION_CONNECTED)
            sendLogout("Responding to Logout");

        disconnect("Logout");
    }

    protected void processAppMessage(Header header, MessageParser parser) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        if (checkTargetSeqNum(header.msgSeqNum(), state.isTargetSeqNumSynchronized())) {
            state.setNextTargetSeqNum(header.msgSeqNum() + 1);
            onAppMessage(header, parser);
        }
    }

    protected void validateHeader(Header header) {
    }

    protected void validateLogon(Logon logon) {
        SessionUtil.validateLogon(heartbeatInterval, logon);
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

        builder.wrap(messageBuffer);
        makeLogon(resetSeqNums, builder);
        sendMessage(true, messageBuffer, 0, builder.length());
        updateStatus(LOGON_SENT);
    }

    protected void sendLogout(CharSequence text) {
        builder.wrap(messageBuffer);
        makeLogout(text, builder);
        sendMessage(true, messageBuffer, 0, builder.length());
        updateStatus(LOGOUT_SENT);
    }

    protected void sendHeartbeat(CharSequence testReqID) {
        builder.wrap(messageBuffer);
        makeHeartbeat(testReqID, builder);
        sendMessage(true, messageBuffer, 0, builder.length());
    }

    protected void sendTestRequest(CharSequence testReqID) {
        builder.wrap(messageBuffer);
        makeTestRequest(testReqID, builder);
        sendMessage(true, messageBuffer, 0, builder.length());
        state.setTestRequestSent(true);
    }

    protected void sendResendRequest(int beginSeqNo, int endSeqNo) {
        builder.wrap(messageBuffer);
        makeResendRequest(beginSeqNo, endSeqNo, builder);
        sendMessage(true, messageBuffer, 0, builder.length());
    }

    protected void sendReject(Buffer buffer, int offset, int length) {
        SessionStatus status = state.getStatus();
        boolean send = (status == APPLICATION_CONNECTED || status == LOGON_SENT);
        sendMessage(send, buffer, offset, length);
    }

    protected void sendSequenceReset(boolean gapFill, int seqNum, int newSeqNo) {
        builder.wrap(messageBuffer);
        makeSequenceReset(gapFill, newSeqNo, builder);
        sendMessage(true, seqNum, messageBuffer, 0, builder.length());
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

    protected void resendMessage(int seqNum, long origSendingTime, ByteSequence msgType, Buffer body, int offset, int length) {
        long time = clock.time();
        int messageLength = packer.pack(seqNum, time, origSendingTime, msgType, body, offset, length);
        sendMessage(time, sendBuffer, 0, messageLength);
    }

    protected void sendMessage(boolean send, int seqNum, Buffer body, int offset, int length) {
        parser.wrap(body, offset, length);
        ByteSequence msgType = parseMessageType(parser, outMsgType);

        int fieldLength = parser.length();
        length -= fieldLength;
        offset += fieldLength;

        long time = clock.time();
        try {
            if (send) {
                int messageLength = packer.pack(seqNum, time, msgType, body, offset, length);
                sendMessage(time, sendBuffer, 0, messageLength);
            }
        } finally {
            if (onStoreMessage(seqNum, time, msgType, body, offset, length))
                store.write(seqNum, time, msgType, body, offset, length);
        }
    }

    protected void sendMessage(long time, Buffer message, int offset, int length) {
        state.setLastSentTime(time);
        sender.send(message, offset, length);
        log.log(false, time, message, offset, length);
    }

    protected void makeLogon(boolean resetSeqNum, MessageBuilder builder) {
        SessionUtil.makeLogon(resetSeqNum, heartbeatInterval, builder);
        if (logonWithNextExpectedSeqNum)
            builder.addInt(Tag.NextExpectedMsgSeqNum, state.getNextTargetSeqNum());
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

    protected SessionStatus updateStatus(SessionStatus status) {
        SessionStatus old = state.getStatus();
        state.setStatus(status);
        onStatusUpdate(old, status);
        return old;
    }

    protected void handleError(Exception exception) {
        if (state.getStatus() != DISCONNECTED)
            disconnect(exception.getMessage());

        onError(exception);
    }

    protected void onStatusUpdate(SessionStatus old, SessionStatus updated) {
    }

    protected void onAdminMessage(Header header, MessageParser parser) {
    }

    protected void onAppMessage(Header header, MessageParser parser) {
    }

    protected boolean onStoreMessage(int seqNum, long sendingTime, ByteSequence msgType, Buffer body, int offset, int length) {
        return !isAdmin(msgType) || msgType.charAt(0) == AdminMessageTypes.REJECT;
    }

    protected boolean onResendMessage(int seqNum, long sendingTime, ByteSequence msgType, Buffer body, int offset, int length) {
        return true;
    }

    protected void onError(Exception exception) {

    }

    protected MessageHandler createInMessageHandler() {
        return (messageType, buffer, offset, length) -> processMessage(buffer, offset, length);
    }

    protected MessageHandler createOutMessageHandler() {
        return (messageType, buffer, offset, length) -> {
            try {
                sendAppMessage(buffer, offset, length);
            } catch (Exception e) {
                handleError(e);
            }
        };
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
