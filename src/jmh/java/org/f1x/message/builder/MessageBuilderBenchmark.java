package org.f1x.message.builder;

import org.f1x.message.field.OrdType;
import org.f1x.message.field.Side;
import org.f1x.message.field.Tag;
import org.f1x.message.field.TimeInForce;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static org.f1x.util.BenchmarkUtil.makeMessage;


@State(Scope.Benchmark)
public class MessageBuilderBenchmark {

    private static final long TRANSACT_TIME = System.currentTimeMillis();
    private static final MutableBuffer BUFFER = UnsafeBuffer.allocateHeap(1024);
    private static final Buffer NEW_ORDER_SINGLE = makeMessage("1=ACCOUNT|11=4|38=5000|40=2|44=400.5|54=1|55=ESH6|58=TEXT|59=0|60=20140603-11:53:03.922|167=FUT|");

    private final MessageBuilder builder = new FastMessageBuilder();

    @Benchmark
    public void encodeNewOrderSingle() {
        builder.wrap(BUFFER);

        builder.addCharSequence(Tag.Account, "ACCOUNT");
        builder.addLong(Tag.ClOrdID, 4);
        builder.addDouble(Tag.OrderQty, 5000.0, 2);
        builder.addByte(Tag.OrdType, OrdType.LIMIT);
        builder.addDouble(Tag.Price, 400.5, 2);
        builder.addByte(Tag.Side, Side.BUY);
        builder.addCharSequence(Tag.Symbol, "ESH6");
        builder.addCharSequence(Tag.Text, "TEXT");
        builder.addByte(Tag.TimeInForce, TimeInForce.DAY);
        builder.addTimestamp(Tag.TransactTime, TRANSACT_TIME);
        builder.addCharSequence(Tag.SecurityType, "FUT");
    }

}
