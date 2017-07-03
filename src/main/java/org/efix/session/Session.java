package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionComponent;
import org.efix.SessionId;
import org.efix.SessionType;
import org.efix.connector.Connector;
import org.efix.connector.channel.Channel;
import org.efix.log.MessageLog;
import org.efix.message.AdminMsgType;
import org.efix.message.FieldException;
import org.efix.message.Header;
import org.efix.message.Message;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.MsgType;
import org.efix.message.field.Tag;
import org.efix.message.parser.MessageParser;
import org.efix.schedule.SessionSchedule;
import org.efix.state.SessionState;
import org.efix.state.SessionStatus;
import org.efix.store.MessageStore;
import org.efix.util.*;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.efix.util.concurrent.Worker;

import java.util.ArrayList;

import static org.efix.session.SessionUtil.assertNotDuplicate;
import static org.efix.session.SessionUtil.parseHeader;
import static org.efix.state.SessionStatus.*;


public abstract class Session implements Worker {

    protected final MessageHandler messageHandler = this::processMessage;

    protected final EpochClock clock;
    protected final SessionState state;
    protected final MessageStore store;
    protected final MessageLog log;
    protected final SessionSchedule schedule;

    protected final Message message = new Message(128);
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
    protected final ByteSequenceWrapper wrapper = new ByteSequenceWrapper();

    protected final SessionType sessionType;
    protected final FixVersion fixVersion;
    protected final SessionId sessionId;
    protected final int heartbeatInterval;
    protected final int inboundHeartbeatTimeout;
    protected final int outboundHeartbeatTimeout;
    protected final int logonTimeout;
    protected final int logoutTimeout;
    protected final boolean resetSeqNumsOnLogon;
    protected final boolean logonWithNextExpectedSeqNum;


    protected final ArrayList<Disposable> openResources = new ArrayList<>();

    protected volatile boolean closing = false;

    public Session(SessionContext context) {
        context.conclude();

        this.clock = context.clock();
        this.state = context.state();
        this.store = context.store();
        this.log = context.log();
        this.schedule = context.schedule();

        this.parser = context.parser();
        this.builder = context.builder();

        this.messageBuffer = UnsafeBuffer.allocateHeap(context.messageBufferSize());
        this.sendBuffer = UnsafeBuffer.allocateDirect(context.sendBufferSize());

        this.connector = context.connector();
        this.receiver = context.receiver();
        this.sender = context.sender();
        this.resender = new Resender(this);
        this.packer = new MessagePacker(context.fixVersion(), context.sessionId(), sendBuffer);

        this.sessionType = context.sessionType();
        this.fixVersion = context.fixVersion();
        this.sessionId = context.sessionId();
        this.heartbeatInterval = context.heartbeatInterval();
        this.inboundHeartbeatTimeout = context.heartbeatInterval() * 1000 + context.maxHeartbeatDelay();
        this.outboundHeartbeatTimeout = context.heartbeatInterval() * 1000;
        this.logonTimeout = context.logonTimeout();
        this.logoutTimeout = context.logoutTimeout();
        this.resetSeqNumsOnLogon = context.resetSeqNumsOnLogon();
        this.logonWithNextExpectedSeqNum = context.logonWithNextExpectedSeqNum();
    }

    @Override
    public void onStart() {
        try {
            open();
        } catch (Exception e) {
            processError(e);
            throw e;
        }
    }

    @Override
    public void onClose() {
        try {
            close();
        } catch (Exception e) {
            processError(e);
            throw e;
        }
    }

    @Override
    public int doWork() {
        int work = work();
        if (work <= 0) {
            flush();
        }

        return work;
    }

    @Override
    public boolean active() {
        return (state.status() != DISCONNECTED) || !closing;
    }

    @Override
    public void deactivate() {
        closing = true;
    }

    protected void open() {
        Disposable[] resources = {state, store, log, connector};
        for (Disposable resource : resources) {
            resource.open();
            openResources.add(resource);
        }
    }

    protected void close() {
        try {
            CloseHelper.close(openResources);
        } finally {
            openResources.clear();
        }
    }

    protected int work() {
        int work = 0;

        work += checkSession(clock.time());
        work += receiveInboundMessages();
        work += sendOutboundMessages();
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
            processError(e);
        }
    }

    protected int checkSession(long now) {
        int work = 0;

        try {
            if (state.status() == DISCONNECTED) {
                work += checkSessionStart(now);
            } else {
                work += checkSessionEnd(now);
            }
        } catch (Exception e) {
            work += 1;
            processError(e);
        }

        return work;
    }

    protected int receiveInboundMessages() {
        int work = 0;

        if (state.status() != DISCONNECTED) {
            try {
                work += receiver.receive(messageHandler);
            } catch (Exception e) {
                work += 1;
                processError(e);
            }
        }

        return work;
    }

    protected int processTimers(long now) {
        int work = 0;

        try {
            SessionStatus status = state.status();
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
            processError(e);
        }

        return work;
    }

    protected int checkSessionStart(long now) {
        int work = 0;
        long start = schedule.getStartTime(now);
        if (canStartSession(now) && now >= start && !closing) {
            boolean connected = connect();
            if (connected) {
                boolean newSession = (state.sessionStartTime() < start);
                if (newSession) {
                    state.targetSeqNum(1);
                    state.senderSeqNum(1);
                    store.clear();
                }

                state.sessionStartTime(now);

                if (sessionType.initiator()) {
                    if (resetSeqNumsOnLogon) {
                        state.targetSeqNum(1);
                    }

                    sendLogon(resetSeqNumsOnLogon);
                }

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

        long end = schedule.getEndTime(state.sessionStartTime());
        if (canEndSession(now) || now >= end || closing) {
            SessionStatus status = state.status();
            if (status == SOCKET_CONNECTED || status == LOGON_SENT) {
                sendLogout("Session end");
                disconnect("Session end");
                work += 1;
            } else if (status == APPLICATION_CONNECTED) {
                sendLogout("Session end");
                work += 1;
            }
        }

        return work;
    }

    protected int checkLogonTimeout(long now) {
        int work = 0;
        long elapsed = now - state.sessionStartTime();
        if (elapsed >= logonTimeout) {
            throw new TimeoutException(String.format("Logon timeout %s ms. Elapsed %s ms", logonTimeout, elapsed));
        }

        return work;
    }

    protected int checkLogoutTimeout(long now) {
        int work = 0;
        long elapsed = now - state.lastSentTime();
        if (elapsed >= logoutTimeout) {
            throw new TimeoutException(String.format("Logout timeout %s ms. Elapsed %s ms", logoutTimeout, elapsed));
        }

        return work;
    }

    protected int checkInHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.lastReceivedTime();
        if (elapsed >= 2 * inboundHeartbeatTimeout) {
            throw new TimeoutException(String.format("No response on TestRequest. Heartbeat timeout %s ms. " +
                    "Elapsed time since last received message %s ms", inboundHeartbeatTimeout, elapsed));
        }

        if (elapsed >= inboundHeartbeatTimeout && !state.testRequestSent()) {
            sendTestRequest("Heartbeat timeout");
            work += 1;
        }

        return work;
    }

    protected int checkOutHeartbeatTimeout(long now) {
        int work = 0;
        long elapsed = now - state.lastSentTime();
        if (elapsed >= outboundHeartbeatTimeout) {
            sendHeartbeat(null);
            work += 1;
        }

        return work;
    }

    protected boolean connect() {
        Channel channel = null;

        if (!connector.isConnectionInitiated()) {
            connector.initiateConnect();
        }

        if (connector.isConnectionPending()) {
            channel = connector.finishConnect();
        }

        boolean connected = (channel != null);
        if (connected) {
            receiver.channel(channel);
            sender.channel(channel);
            updateStatus(SOCKET_CONNECTED);
        }

        return connected;
    }

    protected void disconnect(CharSequence cause) {
        SessionStatus status = state.status();
        if (status != SOCKET_CONNECTED) {
            updateStatus(SOCKET_CONNECTED);
        }

        connector.disconnect();
        receiver.channel(null);
        sender.channel(null);

        updateStatus(DISCONNECTED);
    }

    protected void processMessage(Buffer buffer, int offset, int length) {
        long time = clock.time();
        state.lastReceivedTime(time);
        log.log(true, time, buffer, offset, length);

        Message message = this.message;
        message.parse(buffer, offset, length);

        Header header = this.header;
        parseHeader(message, header);

        if (AdminMsgType.isAdmin(header.msgType())) {
            processAdminMessage(header, message);
        } else {
            processAppMessage(header, message);
        }
    }

    protected void processAdminMessage(Header header, Message message) {
        switch (header.msgType().charAt(0)) {
            case AdminMsgType.LOGON:
                processLogon(header, message);
                break;

            case AdminMsgType.HEARTBEAT:
                processHeartbeat(header, message);
                break;

            case AdminMsgType.TEST:
                processTestRequest(header, message);
                break;

            case AdminMsgType.RESEND:
                processResendRequest(header, message);
                break;

            case AdminMsgType.REJECT:
                processReject(header, message);
                break;

            case AdminMsgType.RESET:
                processSequenceReset(header, message);
                break;

            case AdminMsgType.LOGOUT:
                processLogout(header, message);
                break;
        }
    }

    protected void processLogon(Header header, Message message) {
        assertStatus(SOCKET_CONNECTED, LOGON_SENT);
        assertNotDuplicate(header.possDup(), "Logon with PossDup(44)=Y");

        final boolean resetSeqNums = message.getBool(Tag.ResetSeqNumFlag, false);
        final int msgSeqNum = header.msgSeqNum();

        boolean expectedSeqNum = checkTargetSeqNum(resetSeqNums ? 1 : state.targetSeqNum(), msgSeqNum, resetSeqNums);
        if (!resetSeqNums && expectedSeqNum) {
            state.targetSeqNum(msgSeqNum + 1);
        }

        validateLogon(message);
        onAdminMessage(header, message);

        state.targetSeqNumSynced(false);
        if (resetSeqNums) {
            state.targetSeqNum(msgSeqNum + 1);
        }

        if (updateStatus(LOGON_RECEIVED) == SOCKET_CONNECTED) {
            sendLogon(resetSeqNums);
        }

        if (!expectedSeqNum) {
            sendResendRequest(state.targetSeqNum(), 0);
        }

        sendTestRequest("MsgSeqNum check");
        updateStatus(APPLICATION_CONNECTED);
    }

    protected void processHeartbeat(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Heartbeat with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        checkTargetSeqNum(msgSeqNum, true);

        state.targetSeqNum(msgSeqNum + 1);
        state.testRequestSent(false);
        state.targetSeqNumSynced(true);

        onAdminMessage(header, message);
    }

    protected void processTestRequest(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "TestRequest with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.targetSeqNumSynced())) {
            state.targetSeqNum(msgSeqNum + 1);
        }

        onAdminMessage(header, message);
        if (state.status() == APPLICATION_CONNECTED) {
            CharSequence reqId = message.getString(Tag.TestReqID, wrapper);
            sendHeartbeat(reqId);
        }
    }

    protected void processResendRequest(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "ResendRequest with PossDup(44)=Y");

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.targetSeqNumSynced())) {
            state.targetSeqNum(msgSeqNum + 1);
        }

        validateResendRequest(message);
        onAdminMessage(header, message);

        int beginSeqNo = message.getUInt(Tag.BeginSeqNo);
        int endSeqNo = message.getUInt(Tag.EndSeqNo);

        resendMessages(beginSeqNo, endSeqNo == 0 ? (state.senderSeqNum() - 1) : endSeqNo);
    }

    protected void processReject(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        int msgSeqNum = header.msgSeqNum();
        if (checkTargetSeqNum(msgSeqNum, state.targetSeqNumSynced())) {
            state.targetSeqNum(msgSeqNum + 1);
            onAdminMessage(header, message);
        }
    }

    protected void processSequenceReset(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        boolean gapFill = message.getBool(Tag.GapFillFlag, false);
        if (gapFill) {
            checkTargetSeqNum(header.msgSeqNum(), true);
        }

        validateSequenceReset(message);
        onAdminMessage(header, message);

        int newSeqNo = message.getUInt(Tag.NewSeqNo);
        state.targetSeqNum(newSeqNo);
    }

    protected void processLogout(Header header, Message message) {
        assertStatus(LOGON_SENT, APPLICATION_CONNECTED, LOGOUT_SENT);
        assertNotDuplicate(header.possDup(), "Logout with PossDup(44)=Y");

        boolean expectedSeqNum = checkTargetSeqNum(header.msgSeqNum(), state.targetSeqNumSynced());
        if (expectedSeqNum) {
            state.targetSeqNum(header.msgSeqNum() + 1);
        }

        onAdminMessage(header, message);

        if (updateStatus(LOGOUT_RECEIVED) == APPLICATION_CONNECTED) {
            sendLogout("Logout response");
        }

        disconnect("Logout");
    }

    protected void processAppMessage(Header header, Message message) {
        assertStatus(APPLICATION_CONNECTED, LOGOUT_SENT);

        if (checkTargetSeqNum(header.msgSeqNum(), state.targetSeqNumSynced())) {
            state.targetSeqNum(header.msgSeqNum() + 1);
            onAppMessage(header, message);
        }
    }

    protected int sendOutboundMessages() {
        int work = 0;

        try {
            work += doSendOutboundMessages();
        } catch (Exception e) {
            work += 1;
            processError(e);
        }

        return work;
    }

    protected void sendLogon(boolean resetSeqNums) {
        if (resetSeqNums) {
            state.senderSeqNum(1);
            store.clear();
        }

        builder.wrap(messageBuffer);
        makeLogon(resetSeqNums, builder);
        sendMessage(MsgType.LOGON, messageBuffer, 0, builder.length());
        updateStatus(LOGON_SENT);
    }

    protected void sendLogout(CharSequence text) {
        builder.wrap(messageBuffer);
        makeLogout(text, builder);
        sendMessage(MsgType.LOGOUT, messageBuffer, 0, builder.length());
        updateStatus(LOGOUT_SENT);
    }

    protected void sendHeartbeat(CharSequence testReqID) {
        builder.wrap(messageBuffer);
        makeHeartbeat(testReqID, builder);
        sendMessage(MsgType.HEARTBEAT, messageBuffer, 0, builder.length());
    }

    protected void sendTestRequest(CharSequence testReqID) {
        builder.wrap(messageBuffer);
        makeTestRequest(testReqID, builder);
        sendMessage(MsgType.TEST_REQUEST, messageBuffer, 0, builder.length());
        state.testRequestSent(true);
    }

    protected void sendResendRequest(int beginSeqNo, int endSeqNo) {
        builder.wrap(messageBuffer);
        makeResendRequest(beginSeqNo, endSeqNo, builder);
        sendMessage(MsgType.RESEND_REQUEST, messageBuffer, 0, builder.length());
    }

    protected void sendReject(Buffer buffer, int offset, int length) {
        sendMessage(MsgType.REJECT, buffer, offset, length);
    }

    protected void sendSequenceReset(boolean gapFill, int seqNum, int newSeqNo) {
        builder.wrap(messageBuffer);
        makeSequenceReset(gapFill, newSeqNo, builder);

        long time = clock.time();
        int messageLength = packer.pack(seqNum, time, MsgType.SEQUENCE_RESET, messageBuffer, 0, builder.length());

        sendRawMessage(time, sendBuffer, 0, messageLength);
    }

    protected void sendAppMessage(ByteSequence msgType, Buffer buffer, int offset, int length) {
        try {
            sendMessage(msgType, buffer, offset, length);
        } catch (Exception e) {
            processError(e);
            throw e;
        }
    }

    protected void resendMessages(int beginSeqNo, int endSeqNo) {
        resender.resendMessages(beginSeqNo, endSeqNo, store);
    }

    protected void resendMessage(int seqNum, long origTime, ByteSequence msgType, Buffer body, int offset, int length) {
        long time = clock.time();
        int messageLength = packer.pack(seqNum, time, origTime, msgType, body, offset, length);
        sendRawMessage(time, sendBuffer, 0, messageLength);
    }

    protected void sendMessage(ByteSequence msgType, Buffer body, int offset, int length) {
        int seqNum = state.senderSeqNum();
        long time = clock.time();

        int messageLength = packer.pack(seqNum, time, msgType, body, offset, length);

        sendRawMessage(time, sendBuffer, 0, messageLength);
        state.senderSeqNum(seqNum + 1);

        if (onStoreMessage(seqNum, time, msgType, body, offset, length)) {
            store.write(seqNum, time, msgType, body, offset, length);
        }
    }

    protected void sendRawMessage(long time, Buffer message, int offset, int length) {
        state.lastSentTime(time);
        sender.send(message, offset, length);
        log.log(false, time, message, offset, length);
    }

    protected void validateLogon(Message message) {
        ByteSequenceWrapper wrapper = this.wrapper;

        ByteSequence actual = message.getString(Tag.BeginString, wrapper);
        ByteSequence expected = fixVersion.beginString();
        if (!actual.equals(expected)) {
            throw new FieldException(Tag.BeginString, "BeginString(8) does not match, expected " + expected + " but received " + actual);
        }

        actual = message.getString(Tag.SenderCompID, wrapper);
        expected = sessionId.targetCompId();
        if (!actual.equals(expected)) {
            throw new FieldException(Tag.SenderCompID, "SenderCompID(49) does not match, expected " + expected + " but received " + actual);
        }

        actual = message.getString(Tag.TargetCompID, wrapper);
        expected = sessionId.senderCompId();
        if (!actual.equals(expected)) {
            throw new FieldException(Tag.TargetCompID, "TargetCompID(46) does not match, expected " + expected + " but received " + actual);
        }

        int heartBtInt = message.getUInt(Tag.HeartBtInt);
        if (heartBtInt != heartbeatInterval) {
            throw new FieldException(Tag.HeartBtInt, "HeartBtInt(108) does not match. Expected " + heartbeatInterval + " but received " + heartBtInt);
        }
    }

    protected void validateResendRequest(Message message) {
        int beginSeqNo = message.getUInt(Tag.BeginSeqNo);
        int endSeqNo = message.getUInt(Tag.EndSeqNo);

        if (endSeqNo != 0 && beginSeqNo > endSeqNo) {
            throw new FieldException(Tag.EndSeqNo, "BeginSeqNo(7) " + beginSeqNo + " more EndSeqNo(16) " + endSeqNo);
        }
    }

    protected void validateSequenceReset(Message message) {
        int newSeqNo = message.getUInt(Tag.NewSeqNo);
        int targetSeqNum = state.targetSeqNum();

        if (newSeqNo <= targetSeqNum) {
            throw new FieldException(Tag.NewSeqNo, "NewSeqNo(36) " + newSeqNo + " should be more expected target MsgSeqNum " + targetSeqNum);
        }
    }

    protected void makeLogon(boolean resetSeqNum, MessageBuilder builder) {
        SessionUtil.makeLogon(resetSeqNum, heartbeatInterval, builder);
        if (logonWithNextExpectedSeqNum) {
            builder.addInt(Tag.NextExpectedMsgSeqNum, state.targetSeqNum());
        }
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
        SessionStatus old = state.status();
        state.status(status);
        onStatusUpdate(old, status);
        return old;
    }

    protected void processError(Exception e) {
        if (state.status() != DISCONNECTED) {
            disconnect(e.getMessage());
        }

        onError(e);
    }

    protected boolean canStartSession(long now) {
        return true;
    }

    protected boolean canEndSession(long now) {
        return false;
    }

    protected abstract void onStatusUpdate(SessionStatus previous, SessionStatus current);

    protected abstract int doSendOutboundMessages();

    protected abstract void onAdminMessage(Header header, Message message);

    protected abstract void onAppMessage(Header header, Message message);

    protected boolean onStoreMessage(int seqNum, long sendingTime, ByteSequence msgType, Buffer body, int offset, int length) {
        return !AdminMsgType.isAdmin(msgType) || MsgType.REJECT.equals(msgType);
    }

    protected boolean onResendMessage(int seqNum, long sendingTime, ByteSequence msgType, Buffer body, int offset, int length) {
        return true;
    }

    protected abstract void onError(Exception e);

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2) {
        SessionUtil.assertStatus(expected1, expected2, state.status());
    }

    protected void assertStatus(SessionStatus expected1, SessionStatus expected2, SessionStatus expected3) {
        SessionUtil.assertStatus(expected1, expected2, expected3, state.status());
    }

    protected boolean checkTargetSeqNum(int actual, boolean checkHigher) {
        return SessionUtil.checkTargetSeqNum(state.targetSeqNum(), actual, checkHigher);
    }

    protected boolean checkTargetSeqNum(int expected, int actual, boolean checkHigher) {
        return SessionUtil.checkTargetSeqNum(expected, actual, checkHigher);
    }

}
