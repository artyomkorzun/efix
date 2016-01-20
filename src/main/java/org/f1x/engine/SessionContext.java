package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.connector.AcceptorConnector;
import org.f1x.connector.Connector;
import org.f1x.connector.InitiatorConnector;
import org.f1x.connector.channel.SocketOptions;
import org.f1x.log.EmptyMessageLog;
import org.f1x.log.MessageLog;
import org.f1x.message.builder.FastMessageBuilder;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.parser.FastMessageParser;
import org.f1x.message.parser.MessageParser;
import org.f1x.schedule.ContinuousSessionSchedule;
import org.f1x.schedule.SessionSchedule;
import org.f1x.state.MemorySessionState;
import org.f1x.state.SessionState;
import org.f1x.store.EmptyMessageStore;
import org.f1x.store.MessageStore;
import org.f1x.util.EpochClock;
import org.f1x.util.SystemEpochClock;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.concurrent.ProducerType;
import org.f1x.util.concurrent.buffer.MPSCRingBuffer;
import org.f1x.util.concurrent.buffer.RingBuffer;
import org.f1x.util.concurrent.buffer.SPSCRingBuffer;
import org.f1x.util.concurrent.strategy.BackoffIdleStrategy;
import org.f1x.util.concurrent.strategy.IdleStrategy;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;

import static java.util.Objects.requireNonNull;

public class SessionContext {

    protected static final int BACKOFF_IDLE_STRATEGY_MAX_SPINS = 20;
    protected static final int BACKOFF_IDLE_STRATEGY_MAX_YIELDS = 50;
    protected static final int BACKOFF_IDLE_STRATEGY_MIN_PARK_PERIOD_NS = 1;
    protected static final int BACKOFF_IDLE_STRATEGY_MAX_PARK_PERIOD_NS = 10000;

    protected EpochClock clock;
    protected SessionSchedule schedule;
    protected SessionState state;
    protected MessageStore store;
    protected MessageLog log;

    protected RingBuffer messageQueue;
    protected ProducerType producerType = ProducerType.MULTI;
    protected int messageQueueSize = Configuration.MESSAGE_QUEUE_SIZE;
    protected IdleStrategy idleStrategy;

    protected MessageParser parser;
    protected MessageBuilder builder;

    protected Connector connector;
    protected InetSocketAddress address;
    protected int reconnectInterval = Configuration.RECONNECT_INTERVAL;
    protected int socketReceiveBufferSize = Configuration.SOCKET_RECEIVE_BUFFER_SIZE;
    protected int socketSendBufferSize = Configuration.SOCKET_SEND_BUFFER_SIZE;
    protected boolean socketTcpNoDelay = Configuration.SOCKET_TCP_NO_DELAY;

    protected int messageBufferSize = Configuration.MESSAGE_BUFFER_SIZE;
    protected int receiveBufferSize = Configuration.RECEIVE_BUFFER_SIZE;
    protected int sendBufferSize = Configuration.SEND_BUFFER_SIZE;

    protected SessionID sessionID;
    protected FIXVersion fixVersion;
    protected boolean initiator = true;
    protected int heartbeatInterval = Configuration.HEARTBEAT_INTERVAL;
    protected int heartbeatTimeout = Configuration.HEARTBEAT_TIMEOUT;
    protected int logonTimeout = Configuration.LOGON_TIMEOUT;
    protected int logoutTimeout = Configuration.LOGOUT_TIMEOUT;
    protected boolean resetSeqNumsOnLogon;
    protected boolean logonWithNextExpectedSeqNum;

    public SessionContext(boolean initiator, InetSocketAddress address, SessionID sessionID, FIXVersion fixVersion) {
        this.initiator = initiator;
        this.address = address;
        this.fixVersion = fixVersion;
        this.sessionID = sessionID;
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

    public RingBuffer messageQueue() {
        return messageQueue;
    }

    public SessionContext messageQueue(RingBuffer messageQueue) {
        this.messageQueue = messageQueue;
        return this;
    }

    public ProducerType producerType() {
        return producerType;
    }

    public SessionContext producerType(ProducerType producerType) {
        this.producerType = producerType;
        return this;
    }

    public int messageQueueSize() {
        return messageQueueSize;
    }

    public SessionContext messageQueueSize(int messageQueueSize) {
        this.messageQueueSize = messageQueueSize;
        return this;
    }

    public IdleStrategy idleStrategy() {
        return idleStrategy;
    }

    public SessionContext idleStrategy(IdleStrategy idleStrategy) {
        this.idleStrategy = idleStrategy;
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

    public InetSocketAddress address() {
        return address;
    }

    public SessionContext address(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public int reconnectInterval() {
        return reconnectInterval;
    }

    public SessionContext reconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
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

    public SessionID sessionID() {
        return sessionID;
    }

    public SessionContext sessionID(SessionID sessionID) {
        this.sessionID = sessionID;
        return this;
    }

    public FIXVersion fixVersion() {
        return fixVersion;
    }

    public SessionContext fixVersion(FIXVersion fixVersion) {
        this.fixVersion = fixVersion;
        return this;
    }

    public boolean initiator() {
        return initiator;
    }

    public SessionContext initiator(boolean initiator) {
        this.initiator = initiator;
        return this;
    }

    public int heartbeatInterval() {
        return heartbeatInterval;
    }

    public SessionContext heartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public int heartbeatTimeout() {
        return heartbeatTimeout;
    }

    public SessionContext heartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
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

    public SessionContext conclude() {
        requireNonNull(fixVersion);
        requireNonNull(sessionID);
        requireNonNull(sessionID.senderCompId());
        requireNonNull(sessionID.targetCompId());

        if (clock == null)
            clock = SystemEpochClock.INSTANCE;

        if (state == null)
            state = new MemorySessionState();

        if (store == null)
            store = EmptyMessageStore.INSTANCE; // TODO replace by Memory

        if (schedule == null)
            schedule = ContinuousSessionSchedule.INSTANCE;

        if (log == null)
            log = EmptyMessageLog.INSTANCE;

        if (messageQueue == null) {
            UnsafeBuffer buffer = UnsafeBuffer.allocateHeap(messageQueueSize);
            messageQueue = (producerType == ProducerType.SINGLE) ? new SPSCRingBuffer(buffer) : new MPSCRingBuffer(buffer);
        }

        if (idleStrategy == null) {
            idleStrategy = new BackoffIdleStrategy(
                    BACKOFF_IDLE_STRATEGY_MAX_SPINS,
                    BACKOFF_IDLE_STRATEGY_MAX_YIELDS,
                    BACKOFF_IDLE_STRATEGY_MIN_PARK_PERIOD_NS,
                    BACKOFF_IDLE_STRATEGY_MAX_PARK_PERIOD_NS
            );
        }

        if (parser == null)
            parser = new FastMessageParser();

        if (builder == null)
            builder = new FastMessageBuilder();

        if (connector == null) {
            SocketOptions options = new SocketOptions();
            options.add(StandardSocketOptions.TCP_NODELAY, socketTcpNoDelay);
            options.add(StandardSocketOptions.SO_RCVBUF, socketReceiveBufferSize);
            options.add(StandardSocketOptions.SO_SNDBUF, socketSendBufferSize);

            connector = initiator ?
                    new InitiatorConnector(address, options, clock, reconnectInterval) :
                    new AcceptorConnector(address, options);
        }

        return this;
    }

}
