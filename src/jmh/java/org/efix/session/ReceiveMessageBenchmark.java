package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.SessionType;
import org.efix.connector.ConnectionException;
import org.efix.connector.channel.Channel;
import org.efix.message.Header;
import org.efix.state.SessionStatus;
import org.efix.util.BenchmarkUtil;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 5)
public class ReceiveMessageBenchmark {

    private static final String MESSAGE = "8=FIX.4.4|9=177|35=D|34=1000|49=Sender|56=Receiver|52=20170220-09:15:33.705|1=account|11=1|38=1000|40=2|44=1.2345|54=1|55=EUR/USD|59=0|60=20170220-09:15:33.704|76=cfh|100=#INSTANT-FILL|167=FOR|10=188|";

    private ReceivingSession session;

    @Setup
    public void init() {
        SessionId sessionId = new SessionId("Receiver", "Sender");
        SessionContext context = new SessionContext("localhost", 10000, SessionType.INITIATOR, FixVersion.FIX44, sessionId);
        PredefinedChannel channel = new PredefinedChannel(MESSAGE);
        session = new ReceivingSession(context);
        session.state.status(SessionStatus.APPLICATION_CONNECTED);
        session.receiver.channel(channel);
    }

    @Benchmark
    public void receiveMessage() {
        session.state.targetSeqNum(1000);
        session.receiveInboundMessages();
    }

    private static final class ReceivingSession extends Session {

        private final Message message = new Message();
        private final MessageCodec codec = new MessageCodec();

        public ReceivingSession(SessionContext context) {
            super(context);
        }

        @Override
        protected void onStatusUpdate(SessionStatus previous, SessionStatus current) {
        }

        @Override
        protected int doSendOutboundMessages() {
            return 0;
        }

        @Override
        protected void onAdminMessage(Header header, org.efix.message.Message message) {
        }

        @Override
        protected void onAppMessage(Header header, org.efix.message.Message message) {
            codec.decodeWithIndexMap(this.message, message);
        }

        @Override
        protected void onError(Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static final class PredefinedChannel implements Channel {

        private final Buffer message;

        public PredefinedChannel(String message) {
            this.message = BenchmarkUtil.makeMessage(message);
        }

        @Override
        public int read(MutableBuffer buffer, int offset, int length) throws ConnectionException {
            int capacity = message.capacity();
            buffer.putBytes(offset, message, 0, capacity);
            return capacity;
        }

        @Override
        public int write(Buffer buffer, int offset, int length) throws ConnectionException {
            return 0;
        }

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReceiveMessageBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
