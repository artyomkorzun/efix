package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.SessionType;
import org.efix.connector.ConnectionException;
import org.efix.connector.channel.Channel;
import org.efix.message.Header;
import org.efix.message.field.*;
import org.efix.state.SessionStatus;
import org.efix.store.EmptyMessageStore;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Scope;
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
public class SendMessageBenchmark {

    private SendSession session;

    @Setup
    public void init() {
        SessionId sessionId = new SessionId("Sender", "Receiver");
        SessionContext context = new SessionContext("localhost", 10000, SessionType.INITIATOR, FixVersion.FIX44, sessionId);
        context.store(EmptyMessageStore.INSTANCE);
        DiscardingChannel channel = new DiscardingChannel();
        session = new SendSession(context);
        session.state.status(SessionStatus.APPLICATION_CONNECTED);
        session.sender.channel(channel);
    }

    @Benchmark
    public void sendMessage() {
        session.state.senderSeqNum(1000);
        session.sendNewOrderMessage();
    }

    private static final class SendSession extends Session {

        private static final long TRANSACT_TIME = System.currentTimeMillis();

        private final Message message;
        private final MessageCodec codec = new MessageCodec();

        {
            Message message = new Message();
            message.setAccount("account");
            message.setOrderId("1");
            message.setQuantity(1000_000000);
            message.setOrderType(OrdType.LIMIT);
            message.setLimitPrice(1_234500);
            message.setSide(Side.BUY);
            message.setSymbol("EUR/USD");
            message.setTimeInForce(TimeInForce.DAY);
            message.setTransactTime(TRANSACT_TIME);
            message.setBroker("cfh");
            message.setExchange("#INSTANT-FILL");
            message.setSecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT);

            this.message = message;
        }

        public SendSession(SessionContext context) {
            super(context);
        }

        @Override
        protected void onStatusUpdate(SessionStatus previous, SessionStatus current) {
        }

        @Override
        protected int doSendMessages() {
            return 0;
        }

        @Override
        protected void onAdminMessage(Header header, org.efix.message.Message message) {
        }

        @Override
        protected void onAppMessage(Header header, org.efix.message.Message message) {
        }

        @Override
        protected void onError(Exception e) {
            throw new RuntimeException(e);
        }

        private void sendNewOrderMessage() {
            builder.wrap(messageBuffer);
            codec.encode(message, builder);
            sendAppMessage(MsgType.ORDER_SINGLE, messageBuffer, 0, builder.length());
        }

    }

    private static final class DiscardingChannel implements Channel {


        @Override
        public int read(MutableBuffer buffer, int offset, int length) throws ConnectionException {
            return 0;
        }

        @Override
        public int write(Buffer buffer, int offset, int length) throws ConnectionException {
            return length;
        }

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SendMessageBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
