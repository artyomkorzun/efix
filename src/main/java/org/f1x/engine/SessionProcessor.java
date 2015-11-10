package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionSettings;
import org.f1x.connector.Connector;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.EncryptMethod;
import org.f1x.message.fields.FixTags;
import org.f1x.message.fields.MsgType;
import org.f1x.message.parser.MessageParser;
import org.f1x.message.parser.MessageParsers;
import org.f1x.schedule.SessionSchedule;
import org.f1x.state.SessionState;
import org.f1x.state.SessionStatus;
import org.f1x.store.MessageStore;
import org.f1x.util.ByteArrayReference;
import org.f1x.util.EpochClock;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.concurrent.IdleStrategy;
import org.f1x.util.concurrent.Reader;
import org.f1x.util.concurrent.RingBuffer;
import org.f1x.util.concurrent.Worker;

import static org.f1x.state.SessionStatus.*;
import static org.f1x.util.Checker.checkPositive;
import static org.f1x.util.Checker.checkPresent;

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
    protected final MutableBuffer messageBuffer;
    protected final Receiver receiver;
    protected final Sender sender;

    protected final Header header = new Header();
    protected final Logon logon = new Logon();
    protected final TestRequest testRequest = new TestRequest();
    protected final ResendRequest resendRequest = new ResendRequest();
    protected final SequenceReset sequenceReset = new SequenceReset();
    protected final ByteArrayReference msgTypeToSend = new ByteArrayReference();

    protected final Reader inMessageHandler = createInMessageHandler();
    protected final Reader outMessageHandler = createOutMessageHandler();
    protected final MessageStore.Visitor resendMessagesHandler = createResendMessagesHandler();

    protected Channel channel;

    public SessionProcessor(SessionSettings settings, EpochClock clock, SessionSchedule schedule,
                            SessionState state, MessageStore store, MessageLog log,
                            Connector connector, RingBuffer messageQueue, IdleStrategy idleStrategy,
                            MessageParser parser, MessageBuilder builder, MutableBuffer messageBuffer,
                            Receiver receiver, Sender sender) {
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
        this.messageBuffer = messageBuffer;
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void onStart() {
        state.open();
        state.setStatus(DISCONNECTED);
        store.open();
        log.open();
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
        if (status == SOCKET_CONNECTED || status == LOGON_SENT)
            work += checkLogonTimeout(now);
        else if (status == APPLICATION_CONNECTED)
            work += checkIdleInterval(now);
        else if (status == LOGOUT_SENT)
            work += checkLogoutTimeout(now);

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

    protected int checkIdleInterval(long now) {
        int work = 0;

        work += checkLastReceivedTime(now);
        work += checkLastSentTime(now);

        return work;
    }

    protected int checkLastReceivedTime(long now) {
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

    protected int checkLastSentTime(long now) {
        int work = 0;
        long elapsed = now - state.getLastSentTime();
        int timeout = settings.getHeartbeatTimeout();
        if (elapsed >= timeout) {
            sendHeartbeat(null);
            work += 1;
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

    protected boolean connect() {
        // TODO: polish
        Channel channel = connector.connect();
        if (channel != null) {
            this.channel = channel;
            setStatus(SOCKET_CONNECTED);
        }

        return channel != null;
    }

    protected void disconnect(CharSequence cause) {
        SessionStatus status = state.getStatus();
        if (status != DISCONNECTED) {
            if (status != SOCKET_CONNECTED)
                setStatus(SOCKET_CONNECTED);

            channel.close();
            this.channel = null;
            setStatus(DISCONNECTED);
        }
    }

    protected void processMessage(Buffer buffer, int offset, int length) {
        MessageParser parser = this.parser.wrap(buffer, offset, length);
        Header header = MessageParsers.parseHeader(parser, FIXVersion.FIX44, this.header);
        validateHeader(header);
        processMessage(header, parser.reset());
    }

    protected void processMessage(Header header, MessageParser parser) {
        long time = clock.time();
        state.setLastReceivedTime(time);

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

        Logon logon = MessageParsers.parseLogon(parser, this.logon);
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

        TestRequest request = MessageParsers.parseTestRequest(parser, this.testRequest);
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

        ResendRequest request = MessageParsers.parseResendRequest(parser, this.resendRequest);
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

        SequenceReset reset = MessageParsers.parseSequenceReset(parser, this.sequenceReset);
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

        int heartBtInt = checkPresent(FixTags.HeartBtInt, logon.heartBtInt(), -1);
        int expected = settings.getHeartbeatInterval();
        if (heartBtInt != expected)
            throw new FieldException(FixTags.HeartBtInt, String.format("HeartBtInt does not match, expected %s but received %s", expected, heartBtInt));
    }

    protected void validateTestRequest(TestRequest request) {
        checkPresent(FixTags.TestReqID, request.testReqID());
    }

    protected void validateResendRequest(ResendRequest request) {
        int beginSeqNo = request.beginSeqNo();
        checkPresent(FixTags.BeginSeqNo, beginSeqNo, -1);
        checkPositive(FixTags.BeginSeqNo, beginSeqNo);

        int endSeqNo = request.endSeqNo();
        checkPresent(FixTags.EndSeqNo, endSeqNo, -1);
        checkPositive(FixTags.EndSeqNo, endSeqNo);

        if (endSeqNo != 0 && beginSeqNo > endSeqNo)
            throw new FieldException(FixTags.EndSeqNo, String.format("BeginSeqNo(7) %s is more EndSeqNo(16) %s", beginSeqNo, endSeqNo));
    }

    protected void validateSequenceReset(SequenceReset reset) {
        int newSeqNo = reset.newSeqNo();
        int expected = state.getNextTargetSeqNum();
        if (newSeqNo < expected)
            throw new FieldException(FixTags.NewSeqNo, String.format("NewSeqNo(36) %s less expected MsgSeqNum %s", newSeqNo, expected));
    }

    protected void sendLogon(boolean resetSeqNums) {
        if (resetSeqNums) {
            state.setNextSenderSeqNum(1);
            store.clear();
        }

        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeLogon(resetSeqNums, builder);
        sendMessage(messageBuffer, 0, builder.getLength());
        setStatus(LOGON_SENT);
    }

    protected void sendLogout(CharSequence text) {
        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeLogout(text, builder);
        sendMessage(messageBuffer, 0, builder.getLength());
        setStatus(LOGOUT_SENT);
    }

    protected void sendHeartbeat(CharSequence testReqID) {
        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeHeartbeat(testReqID, builder);
        sendMessage(messageBuffer, 0, builder.getLength());
    }

    protected void sendTestRequest(CharSequence testReqID) {
        state.setTestRequestSent(true);
        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeTestRequest(testReqID, builder);
        sendMessage(messageBuffer, 0, builder.getLength());
    }

    protected void sendResendRequest(int beginSeqNo, int endSeqNo) {
        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeResendRequest(beginSeqNo, endSeqNo, builder);
        sendMessage(messageBuffer, 0, builder.getLength());
    }

    protected void sendReject(Buffer buffer, int offset, int length) {
        boolean send = (state.getStatus() == APPLICATION_CONNECTED);
        sendMessage(send, buffer, offset, length);
    }

    protected void sendSequenceReset(boolean gapFill, int seqNum, int newSeqNo) {
        MessageBuilder builder = this.builder.wrap(messageBuffer);
        makeSequenceReset(gapFill, newSeqNo, builder);
        sendMessage(true, seqNum, messageBuffer, 0, builder.getLength());
    }

    protected void sendAppMessage(Buffer buffer, int offset, int length) {
        boolean send = (state.getStatus() == APPLICATION_CONNECTED);
        sendMessage(send, buffer, offset, length);
    }

    protected void sendMessage(Buffer buffer, int offset, int length) {
        sendMessage(true, buffer, offset, length);
    }

    protected void sendMessage(boolean send, Buffer buffer, int offset, int length) {
        int seqNum = state.getNextSenderSeqNum();
        state.setNextSenderSeqNum(seqNum + 1);
        sendMessage(send, seqNum, buffer, offset, length);
    }

    protected void resendMessages(int beginSeqNo, int endSeqNo) {
        store.read(beginSeqNo, endSeqNo, resendMessagesHandler);
    }

    protected void resendMessage(int seqNum, long origSendingTime, CharSequence msgType, Buffer buffer, int offset, int length) {
        long time = clock.time();
        state.setLastSentTime(time);
        sender.send(seqNum, time, true, origSendingTime, msgType, buffer, offset, length);
    }

    protected void sendMessage(boolean send, int seqNum, Buffer buffer, int offset, int length) {
        MessageParser parser = this.parser.wrap(buffer, offset, length);
        CharSequence msgType = MessageParsers.parseMessageType(parser, msgTypeToSend);
        length -= parser.fieldLength();
        offset += parser.fieldLength();

        long time = clock.time();
        try {
            if (send) {
                state.setLastSentTime(time);
                sender.send(seqNum, time, msgType, buffer, offset, length);
            }
        } finally {
            if (shouldStoreMessage(seqNum, time, msgType, buffer, offset, length))
                store.write(seqNum, time, buffer, offset, length);
        }
    }

    protected void makeLogon(boolean resetSeqNum, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.LOGON);
        builder.add(FixTags.EncryptMethod, EncryptMethod.NONE_OTHER);
        builder.add(FixTags.HeartBtInt, settings.getHeartbeatInterval());
        builder.add(FixTags.ResetSeqNumFlag, resetSeqNum);
        if (settings.isLogonWithNextExpectedSeqNum())
            builder.add(FixTags.NextExpectedMsgSeqNum, state.getNextTargetSeqNum());
    }

    protected void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.HEARTBEAT);
        if (testReqID != null)
            builder.add(FixTags.TestReqID, testReqID);
    }

    protected void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.TEST_REQUEST);
        builder.add(FixTags.TestReqID, testReqID);
    }

    protected void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.RESEND_REQUEST);
        builder.add(FixTags.BeginSeqNo, beginSeqNo);
        builder.add(FixTags.EndSeqNo, endSeqNo);
    }

    protected void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.SEQUENCE_RESET);
        builder.add(FixTags.PossDupFlag, true);
        builder.add(FixTags.NewSeqNo, newSeqNo);
        builder.add(FixTags.GapFillFlag, gapFill);
    }

    protected void makeLogout(CharSequence text, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.LOGOUT);
        if (text != null)
            builder.add(FixTags.Text, text);
    }

    protected void setStatus(SessionStatus status) {
        SessionStatus current = state.getStatus();
        if (current != status) {
            state.setStatus(status);
            onStatusUpdate(current, status);
        }
    }

    protected void onError(Throwable e) {
        disconnect("Error occurred");
    }

    protected void onStatusUpdate(SessionStatus old, SessionStatus fresh) {
    }

    protected void onAdminMessage(Header header, MessageParser parser) {
    }

    protected void onAppMessage(Header header, MessageParser parser) {
    }

    protected boolean shouldStoreMessage(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        return !AdminMessageTypes.isAdmin(msgType) || msgType.charAt(0) == AdminMessageTypes.REJECT;
    }

    protected Reader createInMessageHandler() {
        return (messageType, buffer, offset, length) -> processMessage(buffer, offset, length);
    }

    protected Reader createOutMessageHandler() {
        return (messageType, buffer, offset, length) -> {
            try {
                sendAppMessage(buffer, offset, length);
            } catch (Throwable e) {
                onError(e);
            }
        };
    }

    protected MessageStore.Visitor createResendMessagesHandler() {
        return new MessageStore.Visitor() {
            @Override
            public void onMessage(int seqNum, long sendingTime, Buffer buffer, int offset, int length) {
                resendMessage(seqNum, sendingTime, buffer, offset, length);
            }
        };
    }

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2) {
        SessionStatus status = state.getStatus();
        if (status != expected1 && status != expected2)
            throw new IllegalStateException(String.format("Expected statuses %s and %s but actual %s", expected1, expected2, status));
    }

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2, SessionStatus expected3) {
        SessionStatus status = state.getStatus();
        if (status != expected1 && status != expected2 && status != expected3)
            throw new IllegalStateException(String.format("Expected statuses %s, %s, %s but actual %s", expected1, expected2, expected3, status));
    }

    protected boolean checkTargetSeqNum(int actual, boolean checkHigher) {
        int expected = state.getNextTargetSeqNum();
        if (actual < expected)
            throw new FieldException(FixTags.MsgSeqNum, String.format("MsgSeqNum too low, expecting %s but received %s", expected, actual));

        if (checkHigher && actual > expected)
            throw new FieldException(FixTags.MsgSeqNum, String.format("MsgSeqNum too high, expecting %s but received %s", expected, actual));

        return actual == expected;
    }

    protected static void assertNotDuplicate(boolean possDup, String message) {
        if (possDup)
            throw new FieldException(FixTags.PossDupFlag, message);
    }

}
