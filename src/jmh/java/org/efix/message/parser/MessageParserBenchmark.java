package org.efix.message.parser;

import org.efix.message.field.Tag;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static org.efix.util.BenchmarkUtil.makeMessage;


@State(Scope.Benchmark)
public class MessageParserBenchmark {

    private static final Buffer NEW_ORDER_SINGLE = makeMessage("1=ACCOUNT|11=4|38=5000|40=2|44=400.5|54=1|55=ESH6|58=TEXT|59=0|60=20140603-11:53:03.922|167=FUT|");

    private final MessageParser parser = new FastMessageParser();
    private final NewOrder newOrder = new NewOrder();


    @Benchmark
    public void decodeNewOrderSingle() {
        MessageParser parser = this.parser.wrap(NEW_ORDER_SINGLE);
        NewOrder newOrder = this.newOrder;

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.Account:
                    parser.parseByteSequence(newOrder.account);
                    break;

                case Tag.ClOrdID:
                    newOrder.orderId = parser.parseLong();
                    break;

                case Tag.OrderQty:
                    newOrder.quantity = parser.parseDouble();
                    break;

                case Tag.OrdType:
                    newOrder.orderType = parser.parseByte();
                    break;

                case Tag.Price:
                    newOrder.limitPrice = parser.parseDouble();
                    break;

                case Tag.Side:
                    newOrder.side = parser.parseByte();
                    break;

                case Tag.Symbol:
                    parser.parseByteSequence(newOrder.symbol);
                    break;

                case Tag.Text:
                    parser.parseByteSequence(newOrder.text);
                    break;

                case Tag.TimeInForce:
                    newOrder.timeInForce = parser.parseByte();
                    break;

                case Tag.TransactTime:
                    newOrder.transactTime = parser.parseTimestamp();
                    break;

                case Tag.SecurityType:
                    parser.parseByteSequence(newOrder.securityType);
                    break;

                default:
                    parser.parseValue();
            }
        }
    }

    private static class NewOrder {

        protected final ByteSequenceWrapper account = new ByteSequenceWrapper();
        protected final ByteSequenceWrapper symbol = new ByteSequenceWrapper();
        protected final ByteSequenceWrapper securityType = new ByteSequenceWrapper();
        protected final ByteSequenceWrapper text = new ByteSequenceWrapper();

        protected long orderId;
        protected double quantity;
        protected byte orderType;
        protected double limitPrice;
        protected byte side;
        protected byte timeInForce;
        protected long transactTime;

    }

}
