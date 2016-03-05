package org.efix.util.parse;

import org.efix.util.BenchmarkUtil;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static org.efix.message.FieldUtil.FIELD_SEPARATOR;


@State(Scope.Benchmark)
public class LongParserBenchmark {

    private final MutableInt offset = new MutableInt();

    private String[] strings;
    private Buffer[] buffers;

    @Setup
    public void setup() {
        strings = new String[]{
                "7391639", "-312", "0", "231", "7313", "-654046431",
                "-5743215", "734091", "9192", "-3", "1013", "-5", "8651",
                "33", "-931", "-3031", "981", "10123", "-39187614", "1321311",
                "1321", "7", "-1931913", "-203", "-3495", "-1031", "30313"
        };

        buffers = new Buffer[strings.length];
        for (int i = 0; i < strings.length; i++)
            buffers[i] = BenchmarkUtil.makeMessage(strings[i] + "|");
    }

    @Benchmark
    public void baseLine() {
    }

    @Benchmark
    public void parseLong() {
        for (Buffer buffer : buffers) {
            offset.set(0);
            LongParser.parseLong(FIELD_SEPARATOR, buffer, offset, buffer.capacity());
        }
    }

    @Benchmark
    public void parseLongString() {
        for (String string : strings)
            Long.parseLong(string);
    }

}
