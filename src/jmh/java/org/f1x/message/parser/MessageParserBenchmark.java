package org.f1x.message.parser;

import org.f1x.util.buffer.Buffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static org.f1x.util.BenchmarkUtil.makeMessage;

@State(Scope.Benchmark)
public class MessageParserBenchmark {

    private static final Buffer NEW_ORDER_SINGLE = makeMessage("8=FIX.4.4|9=196|35=D|34=78|49=A12345B|50=2DEFGH4|52=20140603-11:53:03.922|56=COMPARO|57=G|142=AU,SY|1=AU,SY|11=4|21=1|38=50|40=2|44=400.5|54=1|55=OC|58=NIGEL|59=0|60=20140603-11:53:03.922|107=AOZ3 C02000|167=OPT|10=116|");

    private final MessageParser parser = new FastMessageParser();

    @Benchmark
    public void decodeNewOrderSingle() {
        parser.wrap(NEW_ORDER_SINGLE);
        while (parser.hasRemaining()) {
            parser.parseTag();
            parser.parseValue();
        }
    }

}
