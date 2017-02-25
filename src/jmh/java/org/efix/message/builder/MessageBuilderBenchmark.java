package org.efix.message.builder;

import org.efix.message.field.OrdType;
import org.efix.message.field.Side;
import org.efix.message.field.Tag;
import org.efix.message.field.TimeInForce;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.efix.util.BenchmarkUtil.makeMessage;


@State(Scope.Benchmark)
public class MessageBuilderBenchmark {

    private static final long TRANSACT_TIME = System.currentTimeMillis();
    private static final MutableBuffer BUFFER = UnsafeBuffer.allocateHeap(1024);
    private static final Buffer NEW_ORDER_SINGLE = makeMessage("1=ACCOUNT|11=4|38=5000|40=2|44=400.5|54=1|55=ESH6|58=TEXT|59=0|60=20140603-11:53:03.922|167=FUT|");

    @Param({"fast", "checked"})
    private String implementation;

    private MessageBuilder builder;

    @Setup
    public void init() {
        switch (implementation) {
            case "fast":
                builder = new FastMessageBuilder();
                break;
            case "checked":
                builder = new CheckedMessageBuilder(new FastMessageBuilder());
                break;
            default:
                throw new IllegalArgumentException(implementation);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void encodeNewOrderSingle() {
        builder.wrap(BUFFER)
                .addCharSequence(Tag.Account, "ACCOUNT")
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

}
