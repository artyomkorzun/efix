package org.efix.engine;

import org.efix.message.builder.FastMessageBuilder;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.OrdType;
import org.efix.message.field.SecurityType;
import org.efix.message.field.Side;
import org.efix.message.field.TimeInForce;
import org.efix.message.parser.FastMessageParser;
import org.efix.message.parser.MessageParser;
import org.efix.util.BenchmarkUtil;
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
public class MessageCodecBenchmark {

    private static final String MESSAGE = "8=FIX.4.4|9=177|35=D|34=1000|49=Sender|56=Receiver|52=20170220-09:15:33.705|1=account|11=1|38=1000|40=2|44=1.2345|54=1|55=EUR/USD|59=0|60=20170220-09:15:33.704|76=cfh|100=#INSTANT-FILL|167=FOR|10=188|";
    private static final long TRANSACT_TIME = System.currentTimeMillis();

    private final Message message;
    private final MessageCodec codec = new MessageCodec();

    private final MessageParser parser = new FastMessageParser();
    private final MessageBuilder builder = new FastMessageBuilder();

    private final MutableBuffer buffer = BenchmarkUtil.makeMessage(MESSAGE);

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
        message.setSecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT);
        message.setExchange("#INSTANT-FILL");

        this.message = message;
    }

    @Benchmark
    public void encode() {
        builder.wrap(buffer);
        codec.encode(message, builder);
    }

    @Benchmark
    public void decode() {
        parser.wrap(buffer);
        codec.decode(message, parser);
    }

    @Benchmark
    public void decodeWithTable() {
        parser.wrap(buffer);
        codec.decodeWithTable(message, parser);
    }

    @Benchmark
    public void decodeWithUnroll() {
        parser.wrap(buffer);
        codec.decodeWithUnroll(message, parser);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MessageCodecBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
