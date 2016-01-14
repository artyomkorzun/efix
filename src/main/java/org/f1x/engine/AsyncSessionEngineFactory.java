package org.f1x.engine;

import org.f1x.SessionSettings;
import org.f1x.connector.Connector;
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
import org.f1x.util.BufferUtil;
import org.f1x.util.EpochClock;
import org.f1x.util.Factory;
import org.f1x.util.SystemEpochClock;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.concurrent.IdleStrategy;
import org.f1x.util.concurrent.MPSCRingBuffer;
import org.f1x.util.concurrent.RingBuffer;

import static java.util.Objects.requireNonNull;

public class AsyncSessionEngineFactory implements Factory<AsyncSessionEngine> {

    public static final int DEFAULT_MESSAGE_QUEUE_CAPACITY = 1 << 22;

    protected SessionSettings settings;
    protected EpochClock clock;
    protected SessionState state;
    protected MessageStore store;
    protected MessageLog log;
    protected SessionSchedule schedule;
    protected Connector connector;
    protected RingBuffer messageQueue;
    protected IdleStrategy idleStrategy;
    protected MessageParser parser;
    protected MessageBuilder builder;
    protected MutableBuffer messageBuffer;
    protected Receiver receiver;
    protected Sender sender;
    protected MessagePacker packer;

    @Override
    public AsyncSessionEngine create() {
        conclude();

        SessionProcessor processor = new SessionProcessor(
                settings, clock, schedule,
                state, store, log,
                connector, messageQueue, idleStrategy,
                parser, builder, messageBuffer,
                receiver, sender, packer
        );

        return new AsyncSessionEngine(processor, messageQueue);
    }

    public void setSettings(SessionSettings settings) {
        this.settings = settings;
    }

    public void setClock(EpochClock clock) {
        this.clock = clock;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void setStore(MessageStore store) {
        this.store = store;
    }

    public void setLog(MessageLog log) {
        this.log = log;
    }

    public void setSchedule(SessionSchedule schedule) {
        this.schedule = schedule;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public void setMessageQueue(RingBuffer messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void setIdleStrategy(IdleStrategy idleStrategy) {
        this.idleStrategy = idleStrategy;
    }

    public void setParser(MessageParser parser) {
        this.parser = parser;
    }

    public void setBuilder(MessageBuilder builder) {
        this.builder = builder;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setPacker(MessagePacker packer) {
        this.packer = packer;
    }

    protected void conclude() {
        requireNonNull(settings);
        requireNonNull(connector);

        if (clock == null)
            clock = SystemEpochClock.INSTANCE;

        if (state == null)
            state = new MemorySessionState();

        if (store == null)
            store = EmptyMessageStore.INSTANCE;

        if (schedule == null)
            schedule = ContinuousSessionSchedule.INSTANCE;

        if (log == null)
            log = EmptyMessageLog.INSTANCE;

        if (messageBuffer == null)
            messageBuffer = BufferUtil.allocateDirect(1 << 10);

        if (messageQueue == null)
            messageQueue = new MPSCRingBuffer(BufferUtil.allocateDirect(DEFAULT_MESSAGE_QUEUE_CAPACITY));

        if (idleStrategy == null)
            idleStrategy = null; // TODO:

        if (parser == null)
            parser = new FastMessageParser();

        if (builder == null)
            builder = new FastMessageBuilder();

        if (packer == null)
            packer = new MessagePacker(settings.getFixVersion(), settings.getSessionID(), builder, BufferUtil.allocateDirect(1 << 16)); // TODO

        if (receiver == null)
            receiver = new Receiver(parser, BufferUtil.allocateDirect(1 << 16)); // TODO

        if (sender == null)
            sender = new Sender();

    }

}
