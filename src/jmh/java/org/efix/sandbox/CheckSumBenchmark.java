package org.efix.sandbox;

import org.efix.util.UnsafeAccess;
import org.efix.util.buffer.UnsafeBuffer;
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
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckSumBenchmark {

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int array() {
        byte[] array = Config.array;
        int checkSum = 0;

        for (int i = Config.offset, length = Config.length; i < length; i++) {
            checkSum ^= array[i];
        }

        return checkSum & 0xFF;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int buffer() {
        UnsafeBuffer buffer = Config.buffer;
        int checkSum = 0;

        for (int i = Config.offset, length = Config.length; i < length; i++) {
            checkSum ^= buffer.getByte(i);
        }

        return checkSum & 0xFF;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int memoryWithAddress() {
        UnsafeBuffer buffer = Config.buffer;
        int checkSum = 0;

        for (long address = buffer.addressOffset() + Config.offset, limit = address + Config.length; address < limit; address++) {
            checkSum ^= UnsafeAccess.UNSAFE.getByte(address);
        }

        return checkSum & 0xFF;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int memoryWithIndex() {
        UnsafeBuffer buffer = Config.buffer;
        long address = buffer.addressOffset();
        int checkSum = 0;

        for (int i = Config.offset, length = Config.length; i < length; i++) {
            checkSum ^= UnsafeAccess.UNSAFE.getByte(address + i);
        }

        return checkSum & 0xFF;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CheckSumBenchmark.class.getSimpleName())
                .jvmArgsAppend(Config.JVM_ARGS)
                .build();

        new Runner(opt).run();
    }

}
