package org.efix;

import org.HdrHistogram.Histogram;
import org.efix.engine.Session;
import org.efix.engine.SessionContext;
import org.efix.message.Header;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.*;
import org.efix.message.parser.MessageParser;
import org.efix.state.SessionStatus;
import org.efix.util.ByteSequence;
import org.efix.util.concurrent.WorkerRunner;
import org.efix.util.concurrent.strategy.NoOpIdleStrategy;

import static org.efix.message.FieldUtil.LONG_NULL;


public class Ping {

    public static void main(String[] args) {
        SessionId sessionId = new SessionId("Ping", "Pong");
        SessionContext context = new SessionContext(SampleConfiguration.HOST, SampleConfiguration.PORT, SessionType.INITIATOR, FixVersion.FIX44, sessionId);
        context.resetSeqNumsOnLogon(true);
        PingSession session = new PingSession(context);
        WorkerRunner runner = new WorkerRunner(session, new NoOpIdleStrategy());
        new Thread(runner, "Ping").start();
    }

    private static class PingSession extends Session {

        private static final long TRANSACT_TIME = System.currentTimeMillis();

        private final Histogram histogram = new Histogram(1_000_000_000, 3);

        private int warmups;
        private int measures;
        private int iterations;

        private long startMs = Long.MAX_VALUE;
        private long sendNs = Long.MAX_VALUE;
        private long receiveNs = LONG_NULL;

        public PingSession(SessionContext context) {
            super(context);
        }

        @Override
        protected void onStatusUpdate(SessionStatus previous, SessionStatus current) {
            if (current == SessionStatus.APPLICATION_CONNECTED) {
                System.out.printf("Session %s -> %s connected%n", sessionId.senderCompId(), sessionId.targetCompId());
            } else if (current == SessionStatus.DISCONNECTED) {
                System.out.printf("Session %s -> %s disconnected%n", sessionId.senderCompId(), sessionId.targetCompId());
            }
        }

        @Override
        protected int doSendOutboundMessages() {
            if (state.status() == SessionStatus.APPLICATION_CONNECTED && sendNs == LONG_NULL) {
                sendNs = System.nanoTime();

                builder.wrap(messageBuffer);
                makeNewOrderSingle(builder);
                sendAppMessage(MsgType.ORDER_SINGLE, messageBuffer, 0, builder.length());
                return 1;
            }

            return 0;
        }

        @Override
        protected void onAdminMessage(Header header, MessageParser parser) {
        }

        @Override
        protected void onAppMessage(Header header, MessageParser parser) {
            ByteSequence msgType = header.msgType();
            if (MsgType.EXECUTION_REPORT.equals(msgType)) {
                while (parser.hasRemaining()) {
                    parser.parseTag();
                    parser.parseValue();
                }

                receiveNs = System.nanoTime();
            }
        }

        @Override
        protected int processTimers(long now) {
            int work = super.processTimers(now);

            if (sendNs != LONG_NULL && receiveNs != LONG_NULL) {
                histogram.recordValue(receiveNs - sendNs);
                sendNs = LONG_NULL;
                receiveNs = LONG_NULL;
                iterations++;

                if (warmups < SampleConfiguration.WARMUP_REPEATS) {
                    if (iterations > SampleConfiguration.WARMUP_ITERATIONS) {
                        long durationMs = System.currentTimeMillis() - startMs;

                        iterations = 0;
                        warmups++;

                        System.out.printf("Warmup #%s took %s ms\n", warmups, durationMs);
                    }
                } else {
                    if (iterations > SampleConfiguration.MEASURE_ITERATIONS) {
                        long durationMs = System.currentTimeMillis() - startMs;

                        iterations = 0;
                        measures++;

                        System.out.printf("Measure #%s took %s ms\n", measures, durationMs);
                        printStatistic(histogram);
                        System.out.println("-----------------------------------------------");
                        if (measures == SampleConfiguration.MEASURE_REPEATS) {
                            closing = true;
                        }
                    }
                }
            }

            if (iterations == 0) {
                histogram.reset();
                iterations = 1;
                sendNs = LONG_NULL;
                receiveNs = LONG_NULL;
                startMs = System.currentTimeMillis();
            }


            return work;
        }

        @Override
        protected void onError(Exception e) {
            System.err.printf("Error occurred %s%n", e);
        }

        private static void makeNewOrderSingle(MessageBuilder builder) {
            builder.addCharSequence(Tag.Account, "ACCOUNT")
                    .addLong(Tag.ClOrdID, 4)
                    .addDouble(Tag.OrderQty, 5000.0, 2)
                    .addByte(Tag.OrdType, OrdType.LIMIT)
                    .addDouble(Tag.Price, 400.5, 2)
                    .addByte(Tag.Side, Side.BUY)
                    .addCharSequence(Tag.Symbol, "ESH6")
                    .addCharSequence(Tag.Text, "TEXT")
                    .addByte(Tag.TimeInForce, TimeInForce.DAY)
                    .addTimestamp(Tag.TransactTime, TRANSACT_TIME)
                    .addCharSequence(Tag.SecurityType, "FUT");
        }

        public static void printStatistic(Histogram histogram) {
            System.out.printf("\tPercentile (%%)     Value (ns)          Count%n");
            System.out.printf("\t         0.000      %9s      %9s%n", histogram.getValueAtPercentile(0.0), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(0.0)));
            System.out.printf("\t        50.000      %9s      %9s%n", histogram.getValueAtPercentile(50), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(50.0)));
            System.out.printf("\t        90.000      %9s      %9s%n", histogram.getValueAtPercentile(90), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(90.0)));
            System.out.printf("\t        99.000      %9s      %9s%n", histogram.getValueAtPercentile(99), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(99.0)));
            System.out.printf("\t        99.900      %9s      %9s%n", histogram.getValueAtPercentile(99.9), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(99.9)));
            System.out.printf("\t        99.990      %9s      %9s%n", histogram.getValueAtPercentile(99.99), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(99.99)));
            System.out.printf("\t        99.999      %9s      %9s%n", histogram.getValueAtPercentile(99.999), histogram.getCountBetweenValues(0, histogram.getValueAtPercentile(99.999)));
            System.out.printf("\t       100.000      %9s      %9s%n", histogram.getValueAtPercentile(100.0), histogram.getTotalCount());
            System.out.printf("%n\t[Min %s ns, Mean %.0f ns, Max %s ns]%n", histogram.getMinValue(), histogram.getMean(), histogram.getMaxValue());
        }

    }

}
