package org.efix.engine;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.SessionType;
import org.efix.connector.AcceptorConnector;
import org.efix.connector.Connector;
import org.efix.connector.InitiatorConnector;
import org.efix.connector.channel.SocketOptions;
import org.efix.log.EmptyMessageLog;
import org.efix.log.MessageLog;
import org.efix.message.builder.FastMessageBuilder;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.parser.FastMessageParser;
import org.efix.message.parser.MessageParser;
import org.efix.schedule.ContinuousSessionSchedule;
import org.efix.schedule.SessionSchedule;
import org.efix.state.MemorySessionState;
import org.efix.state.SessionState;
import org.efix.store.MemoryMessageStore;
import org.efix.store.MessageStore;
import org.efix.util.EpochClock;
import org.efix.util.SystemEpochClock;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;

import static java.util.Objects.requireNonNull;


public class SessionContext {

    protected EpochClock clock = SystemEpochClock.INSTANCE;
    protected SessionSchedule schedule;
    protected SessionState state;
    protected MessageStore store;
    protected MessageLog log;

    protected MessageParser parser;
    protected MessageBuilder builder;

    protected Connector connector;
    protected SocketAddress address;
    protected int connectInterval = Configuration.CONNECT_INTERVAL_MS;
    protected int socketReceiveBufferSize = Configuration.SOCKET_RECEIVE_BUFFER_SIZE;
    protected int socketSendBufferSize = Configuration.SOCKET_SEND_BUFFER_SIZE;
    protected boolean socketTcpNoDelay = Configuration.SOCKET_TCP_NO_DELAY;
    protected boolean socketKeepAlive = Configuration.SOCKET_KEEP_ALIVE;
    protected boolean socketReuseAddress = Configuration.SOCKET_REUSE_ADDRESS;

    protected int messageBufferSize = Configuration.MESSAGE_BUFFER_SIZE;
    protected int receiveBufferSize = Configuration.RECEIVE_BUFFER_SIZE;
    protected int sendBufferSize = Configuration.SEND_BUFFER_SIZE;
    protected int mtuSize = Configuration.MTU_SIZE;

    protected SessionType sessionType;
    protected FixVersion fixVersion;
    protected SessionId sessionId;
    protected int heartbeatInterval = Configuration.HEARTBEAT_INTERVAL_S;
    protected int maxHeartbeatDelay = Configuration.MAX_HEARTBEAT_DELAY_MS;
    protected int logonTimeout = Configuration.LOGON_TIMEOUT_MS;
    protected int logoutTimeout = Configuration.LOGOUT_TIMEOUT_MS;
    protected int sendTimeout = Configuration.SEND_TIMEOUT_MS;
    protected boolean resetSeqNumsOnLogon;
    protected boolean logonWithNextExpectedSeqNum;

    protected Sender sender;
    protected Receiver receiver;

    public SessionContext(String host, int port, SessionType sessionType, FixVersion fixVersion, SessionId sessionId) {
        this(new InetSocketAddress(host, port), sessionType, fixVersion, sessionId);
    }

    public SessionContext(SocketAddress address, SessionType sessionType, FixVersion fixVersion, SessionId sessionId) {
        this.address = address;
        this.sessionType = sessionType;
        this.fixVersion = fixVersion;
        this.sessionId = sessionId;
    }

    public EpochClock clock() {
        return clock;
    }

    public SessionContext clock(EpochClock clock) {
        this.clock = clock;
        return this;
    }

    public SessionSchedule schedule() {
        return schedule;
    }

    public SessionContext schedule(SessionSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public SessionState state() {
        return state;
    }

    public SessionContext state(SessionState state) {
        this.state = state;
        return this;
    }

    public MessageStore store() {
        return store;
    }

    public SessionContext store(MessageStore store) {
        this.store = store;
        return this;
    }

    public MessageLog log() {
        return log;
    }

    public SessionContext log(MessageLog log) {
        this.log = log;
        return this;
    }

    public MessageParser parser() {
        return parser;
    }

    public SessionContext parser(MessageParser parser) {
        this.parser = parser;
        return this;
    }

    public MessageBuilder builder() {
        return builder;
    }

    public SessionContext builder(MessageBuilder builder) {
        this.builder = builder;
        return this;
    }

    public Connector connector() {
        return connector;
    }

    public SessionContext connector(Connector connector) {
        this.connector = connector;
        return this;
    }

    public SocketAddress address() {
        return address;
    }

    public SessionContext address(String host, int port) {
        return address(new InetSocketAddress(host, port));
    }

    public SessionContext address(SocketAddress address) {
        this.address = address;
        return this;
    }

    public int connectInterval() {
        return connectInterval;
    }

    public SessionContext connectInterval(int connectInterval) {
        this.connectInterval = connectInterval;
        return this;
    }

    public int socketReceiveBufferSize() {
        return socketReceiveBufferSize;
    }

    public SessionContext socketReceiveBufferSize(int socketReceiveBufferSize) {
        this.socketReceiveBufferSize = socketReceiveBufferSize;
        return this;
    }

    public int socketSendBufferSize() {
        return socketSendBufferSize;
    }

    public SessionContext socketSendBufferSize(int socketSendBufferSize) {
        this.socketSendBufferSize = socketSendBufferSize;
        return this;
    }

    public boolean socketTcpNoDelay() {
        return socketTcpNoDelay;
    }

    public SessionContext socketTcpNoDelay(boolean socketTcpNoDelay) {
        this.socketTcpNoDelay = socketTcpNoDelay;
        return this;
    }

    public boolean socketReuseAddress() {
        return socketReuseAddress;
    }

    public void socketReuseAddress(boolean socketReuseAddress) {
        this.socketReuseAddress = socketReuseAddress;
    }

    public boolean socketKeepAlive() {
        return socketKeepAlive;
    }

    public void socketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public int messageBufferSize() {
        return messageBufferSize;
    }

    public SessionContext messageBufferSize(int messageBufferSize) {
        this.messageBufferSize = messageBufferSize;
        return this;
    }

    public int receiveBufferSize() {
        return receiveBufferSize;
    }

    public SessionContext receiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
        return this;
    }

    public int sendBufferSize() {
        return sendBufferSize;
    }

    public SessionContext sendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
        return this;
    }

    public int mtuSize() {
        return mtuSize;
    }

    public SessionContext mtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
        return this;
    }

    public SessionType sessionType() {
        return sessionType;
    }

    public SessionContext sessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public SessionContext sessionId(SessionId sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public FixVersion fixVersion() {
        return fixVersion;
    }

    public SessionContext fixVersion(FixVersion fixVersion) {
        this.fixVersion = fixVersion;
        return this;
    }

    public int heartbeatInterval() {
        return heartbeatInterval;
    }

    public SessionContext heartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public int maxHeartbeatDelay() {
        return maxHeartbeatDelay;
    }

    public SessionContext maxHeartbeatDelay(int maxHeartbeatDelay) {
        this.maxHeartbeatDelay = maxHeartbeatDelay;
        return this;
    }

    public int logonTimeout() {
        return logonTimeout;
    }

    public SessionContext logonTimeout(int logonTimeout) {
        this.logonTimeout = logonTimeout;
        return this;
    }

    public int logoutTimeout() {
        return logoutTimeout;
    }

    public SessionContext logoutTimeout(int logoutTimeout) {
        this.logoutTimeout = logoutTimeout;
        return this;
    }

    public int sendTimeout() {
        return sendTimeout;
    }

    public SessionContext sendTimeout(int sendTimeout) {
        this.sendTimeout = sendTimeout;
        return this;
    }

    public boolean resetSeqNumsOnLogon() {
        return resetSeqNumsOnLogon;
    }

    public SessionContext resetSeqNumsOnLogon(boolean resetSeqNumsOnLogon) {
        this.resetSeqNumsOnLogon = resetSeqNumsOnLogon;
        return this;
    }

    public boolean logonWithNextExpectedSeqNum() {
        return logonWithNextExpectedSeqNum;
    }

    public SessionContext logonWithNextExpectedSeqNum(boolean logonWithNextExpectedSeqNum) {
        this.logonWithNextExpectedSeqNum = logonWithNextExpectedSeqNum;
        return this;
    }

    public Sender sender() {
        return sender;
    }

    public SessionContext sender(Sender sender) {
        this.sender = sender;
        return this;
    }

    public Receiver receiver() {
        return receiver;
    }

    public SessionContext receiver(Receiver receiver) {
        this.receiver = receiver;
        return this;
    }

    public SessionContext conclude() {
        requireNonNull(sessionType);
        requireNonNull(fixVersion);
        requireNonNull(sessionId);
        requireNonNull(sessionId.senderCompId());
        requireNonNull(sessionId.targetCompId());

        if (state == null)
            state = new MemorySessionState();

        if (store == null)
            store = new MemoryMessageStore(Configuration.MESSAGE_STORE_SIZE);

        if (schedule == null)
            schedule = ContinuousSessionSchedule.INSTANCE;

        if (log == null)
            log = EmptyMessageLog.INSTANCE;

        if (parser == null)
            parser = new FastMessageParser();

        if (builder == null)
            builder = new FastMessageBuilder();

        if (connector == null) {
            SocketOptions acceptorOptions = new SocketOptions();
            acceptorOptions.add(StandardSocketOptions.SO_REUSEADDR, socketReuseAddress);

            SocketOptions channelOptions = new SocketOptions();
            channelOptions.add(StandardSocketOptions.TCP_NODELAY, socketTcpNoDelay);
            channelOptions.add(StandardSocketOptions.SO_RCVBUF, socketReceiveBufferSize);
            channelOptions.add(StandardSocketOptions.SO_SNDBUF, socketSendBufferSize);
            channelOptions.add(StandardSocketOptions.SO_KEEPALIVE, socketKeepAlive);

            connector = sessionType.initiator() ?
                    new InitiatorConnector(address, channelOptions, clock, connectInterval) :
                    new AcceptorConnector(address, acceptorOptions, channelOptions, clock, connectInterval);
        }

        if (receiver == null) {
            receiver = new Receiver(receiveBufferSize, mtuSize);
        }

        if (sender == null) {
            sender = new Sender(clock, sendTimeout);
        }

        return this;
    }

}
