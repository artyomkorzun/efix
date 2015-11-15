package org.f1x.message.parser;

import org.f1x.util.StringUtil;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class MessageParserBenchmark {

    private static final String MESSAGE = "8=FIX.4.4|9=196|35=D|34=78|49=A12345B|50=2DEFGH4|52=20140603-11:53:03.922|56=COMPARO|57=G|142=AU,SY|1=AU,SY|11=4|21=1|38=50|40=2|44=400.5|54=1|55=OC|58=NIGEL|59=0|60=20140603-11:53:03.922|107=AOZ3 C02000|167=OPT|10=116|".replace('|', '\u0001');
    private static final Buffer BUFFER = new UnsafeBuffer(StringUtil.asciiBytes(MESSAGE));

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
        parser.wrap(BUFFER);
        while (parser.next())
            parser.tag();
    }

}
