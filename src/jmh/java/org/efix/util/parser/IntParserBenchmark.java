package org.efix.util.parser;

import org.efix.util.buffer.Buffer;
import org.efix.util.parse.IntParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.efix.util.BenchmarkUtil.makeMessage;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 5)
public class IntParserBenchmark {

    private final Buffer buffer = makeMessage("1234567890");

    @Benchmark
    public void decodeLoop() {
        IntParser.parseUInt(1, buffer, 0, 10);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntParserBenchmark.class.getSimpleName())
                .jvmArgsAppend("-Defix.disable.bounds.check=true")
                .build();

        new Runner(opt).run();
    }


}
