package org.f1x.message.parser;

import org.f1x.util.buffer.Buffer;
import org.openjdk.jmh.annotations.*;

import static org.f1x.util.BenchmarkUtil.makeMessage;

@State(Scope.Benchmark)
public class MessageParserBenchmark {

    private static final Buffer NEW_ORDER_SINGLE = makeMessage("8=FIX.4.4|9=196|35=D|34=78|49=A12345B|50=2DEFGH4|52=20140603-11:53:03.922|56=COMPARO|57=G|142=AU,SY|1=AU,SY|11=4|21=1|38=50|40=2|44=400.5|54=1|55=OC|58=NIGEL|59=0|60=20140603-11:53:03.922|107=AOZ3 C02000|167=OPT|10=116|");
    private static final Buffer INT_FIELDS = makeMessage("1=1|2=12|3=123|4=1234|5=12345|6=123456|7=1234567|8=12345678|9=123456789|");

    @Param({"fast", "optimized"})
    private String name;

    private MessageParser parser;

    @Setup
    public void setup() {
        switch (name) {
            case "fast":
                parser = new FastMessageParser();
                break;
            case "optimized":
                parser = new OptimizedMessageParser();
                break;
            default:
                throw new AssertionError(name);
        }
    }

    @Benchmark
    public void decodeNewOrderSingle() {
        parser.wrap(NEW_ORDER_SINGLE);
        while (parser.next())
            parser.tag();
    }

    @Benchmark
    public void decodeIntFields() {
        parser.wrap(INT_FIELDS);
        while (parser.next()) {
            int tag = parser.tag();
            int value = parser.intValue();
            switch (tag) {
                case 1:
                    assertV(1, value);
                    break;
                case 2:
                    assertV(12, value);
                    break;
                case 3:
                    assertV(123, value);
                    break;
                case 4:
                    assertV(1234, value);
                    break;
                case 5:
                    assertV(12345, value);
                    break;
                case 6:
                    assertV(123456, value);
                    break;
                case 7:
                    assertV(1234567, value);
                    break;
                case 8:
                    assertV(12345678, value);
                    break;
                case 9:
                    assertV(123456789, value);
                    break;
                default:
                    throw new AssertionError();
            }

        }
    }

    private static void assertV(int expected, int actual) {
        if (expected != actual)
            throw new AssertionError("expected: " + expected + " actual: "+ actual);
    }

    /*@Benchmark
    public void decodeIntFieldsInStack() {
        Buffer buffer = INT_FIELDS;
        int offset = 0;
        int length = buffer.capacity();

        while (offset < length) {
            int tag = 0;
            while (offset < length) {
                byte b = buffer.getByte(offset++);
                if (b == '=')
                    break;

                tag = (tag << 3) + (tag << 1) + b - '0';
            }

            int value = 0;
            while (offset < length) {
                byte b = buffer.getByte(offset++);
                if (b == '\u0001')
                    break;

                value = (value << 3) + (value << 1) + b - '0';
            }
        }
    }*/

}
