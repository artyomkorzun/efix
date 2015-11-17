package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.parse.newOne.IntParser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static org.f1x.util.BenchmarkUtil.makeMessage;

@State(Scope.Benchmark)
public class IntParserBenchmark {

    private static final Buffer[] POSITIVE_NUMBERS = {
            makeMessage("1="),
            makeMessage("12="),
            makeMessage("123="),
            makeMessage("1234="),
            makeMessage("12345="),
            makeMessage("123456="),
            makeMessage("1234567="),
            makeMessage("12345678="),
            makeMessage("123456789=")
    };

    private static final Buffer[] NEGATIVE_NUMBERS = {
            makeMessage("-1="),
            makeMessage("-12="),
            makeMessage("-123="),
            makeMessage("-1234="),
            makeMessage("-12345="),
            makeMessage("-123456="),
            makeMessage("-1234567="),
            makeMessage("-12345678="),
            makeMessage("-123456789=")
    };

    private static final Buffer[] ALL_NUMBERS = {
            makeMessage("-12345="),
            makeMessage("1="),
            makeMessage("-123456789="),
            makeMessage("1234="),
            makeMessage("-1234="),
            makeMessage("12="),
            makeMessage("-1234567="),
            makeMessage("123456="),
            makeMessage("12345678="),
            makeMessage("1234567="),
            makeMessage("-1="),
            makeMessage("-12="),
            makeMessage("123456789="),
            makeMessage("-123="),
            makeMessage("-123456="),
            makeMessage("12345="),
            makeMessage("-12345678="),
            makeMessage("123=")
    };

    private static final byte SEPARATOR = '=';

    private final MutableInt intOffsetObject = new MutableInt();

    @Benchmark
    public void baseLine() {
    }

    @Benchmark
    public void oldParsePositiveInt() {
        oldParse(POSITIVE_NUMBERS);
    }

    @Benchmark
     public void oldParseNegativeInt() {
        oldParse(NEGATIVE_NUMBERS);
    }

    @Benchmark
    public void oldParseAllInt() {
        oldParse(ALL_NUMBERS);
    }

    @Benchmark
    public void newParsePositiveInt() {
        newParse(POSITIVE_NUMBERS);
    }

    @Benchmark
    public void newParseNegativeInt() {
        newParse(NEGATIVE_NUMBERS);
    }

    @Benchmark
    public void newParseAllInt() {
        newParse(ALL_NUMBERS);
    }

    private void oldParse(Buffer[] buffers) {
        for (Buffer buffer : buffers) {
            int offset = 0;
            int end = buffer.capacity();
            while (offset < end) {
                if (buffer.getByte(offset) == SEPARATOR)
                    break;

                offset++;
            }

            NumbersParser.parseInt(buffer, 0, offset);
        }
    }

    private void newParse(Buffer[] buffers) {
        for (Buffer buffer : buffers) {
            intOffsetObject.value(0);
            IntParser.parseInt(SEPARATOR, buffer, intOffsetObject, buffer.capacity());
        }
    }

}
