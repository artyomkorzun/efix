package org.f1x.engine;

import org.f1x.SessionSettings;
import org.f1x.connector.Connector;
import org.f1x.log.MessageLog;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.parser.MessageParser;
import org.f1x.schedule.SessionSchedule;
import org.f1x.state.SessionState;
import org.f1x.store.MessageStore;
import org.f1x.util.EpochClock;
import org.f1x.util.Factory;
import org.f1x.util.SystemEpochClock;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.concurrent.IdleStrategy;
import org.f1x.util.concurrent.MPSCRingBuffer;
import org.f1x.util.concurrent.RingBuffer;

import java.nio.ByteBuffer;
import java.util.Objects;

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

    protected void conclude() {
        Objects.requireNonNull(settings);
        if (settings == null)
            settings = null; // TODO: set memory

        if (clock == null)
            clock = SystemEpochClock.INSTANCE;

        if (schedule == null)
            schedule = null; // TODO: set continuous

        if (store == null)
            store = null; // TODO: set memory

        if (log == null)
            log = null; // TODO: set null

        if (connector == null)
            connector = null; // TODO: set

        if (messageQueue == null)
            messageQueue = new MPSCRingBuffer(new UnsafeBuffer(ByteBuffer.allocateDirect(DEFAULT_MESSAGE_QUEUE_CAPACITY)));

        if (idleStrategy == null)
            idleStrategy = null; // TODO:


    }

}
