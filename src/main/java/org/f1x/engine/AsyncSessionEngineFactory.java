package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.SessionIDBean;
import org.f1x.SessionSettings;
import org.f1x.connector.AcceptorConnector;
import org.f1x.connector.Connector;
import org.f1x.connector.InitiatorConnector;
import org.f1x.connector.channel.SocketOptions;
import org.f1x.log.EmptyMessageLog;
import org.f1x.log.FileMessageLog;
import org.f1x.log.MessageLog;
import org.f1x.log.layout.TimeLayout;
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
import org.f1x.util.concurrent.buffer.MPSCRingBuffer;
import org.f1x.util.concurrent.buffer.RingBuffer;
import org.f1x.util.concurrent.strategy.IdleStrategy;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.locks.LockSupport;

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
            idleStrategy = workCount -> LockSupport.parkNanos(1000000); // TODO:

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

        if (connector == null)
            connector = settings.isInitiator() ?
                    new InitiatorConnector(15000, clock, new InetSocketAddress(25000), new SocketOptions()) :
                    new AcceptorConnector(new InetSocketAddress(25000), new SocketOptions());

    }

    public static void main(String[] args) {
        SessionID sessionID = new SessionIDBean("TTDEV14O", "DELTIX");
        SessionSettings settings = new SessionSettings();
        settings.setSessionID(sessionID);

        settings.setFixVersion(FIXVersion.FIX42);
        settings.resetSeqNumsOnEachLogon(true);
        settings.setInitiator(false);

        AsyncSessionEngineFactory factory = new AsyncSessionEngineFactory();
        factory.setSettings(settings);
        factory.setLog(new FileMessageLog(1 << 22, Paths.get("D:/f1x-log.messages"), new TimeLayout()));

        AsyncSessionEngine engine = factory.create();
        engine.start();

        System.out.println("Close?");
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        engine.close();
    }

}
