package org.efix.sandbox;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

import static org.efix.util.UnsafeAccess.UNSAFE;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerFormatting {

    private static final byte[] FIRST_DIGIT = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };

    private static final byte[] SECOND_DIGIT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    private static final short[] TWO_DIGITS = {
            12336, 12337, 12338, 12339, 12340, 12341, 12342, 12343, 12344, 12345,
            12592, 12593, 12594, 12595, 12596, 12597, 12598, 12599, 12600, 12601,
            12848, 12849, 12850, 12851, 12852, 12853, 12854, 12855, 12856, 12857,
            13104, 13105, 13106, 13107, 13108, 13109, 13110, 13111, 13112, 13113,
            13360, 13361, 13362, 13363, 13364, 13365, 13366, 13367, 13368, 13369,
            13616, 13617, 13618, 13619, 13620, 13621, 13622, 13623, 13624, 13625,
            13872, 13873, 13874, 13875, 13876, 13877, 13878, 13879, 13880, 13881,
            14128, 14129, 14130, 14131, 14132, 14133, 14134, 14135, 14136, 14137,
            14384, 14385, 14386, 14387, 14388, 14389, 14390, 14391, 14392, 14393,
            14640, 14641, 14642, 14643, 14644, 14645, 14646, 14647, 14648, 14649
    };

    private static final UnsafeBuffer fake = UnsafeBuffer.allocateDirect(200);
    private static final long address = fake.addressOffset();

    static {
        for (int i = 0; i < TWO_DIGITS.length; i++) {
            fake.putShort((i << 1), TWO_DIGITS[i]);
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy10() {
        byte[] array = Configuration.array;
        int offset = Configuration.offset;
        int number = Configuration.integer;

        while (number > 9) {
            array[offset++] = (byte) ('0' + number % 10);
            number /= 10;
        }

        array[offset++] = (byte) ('0' + number);
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100() {
        byte[] array = Configuration.array;
        int offset = Configuration.offset;
        int number = Configuration.integer;

        while (number > 99) {
            int remainder = number % 100;
            short twoDigits = TWO_DIGITS[remainder];
            array[offset++] = (byte) (twoDigits);
            array[offset++] = (byte) (twoDigits >> 8);
            number /= 100;
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            array[offset++] = (byte) (twoDigits);
        }

        array[offset++] = (byte) (twoDigits >> 8);
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeAccess() {
        byte[] array = Configuration.array;
        int offset = Configuration.offset;
        int number = Configuration.integer;

        while (number > 99) {
            int remainder = number % 100;
            short twoDigits = UNSAFE.getShort(TWO_DIGITS, Unsafe.ARRAY_SHORT_BASE_OFFSET + (remainder << 1));
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, twoDigits); // + maximum boost
            offset += 2;
            number /= 100;
        }

        short twoDigits = UNSAFE.getShort(TWO_DIGITS, Unsafe.ARRAY_SHORT_BASE_OFFSET + (number << 1));
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset++, (byte) (twoDigits));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset++, (byte) (twoDigits >> 8));
        //System.out.println(new String(array, offset, offset));
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public long divBy100WithUnsafeAccessToMemory() {
        Buffer buffer = Configuration.buffer;
        long offset = buffer.addressOffset() + Configuration.offset;
        int number = Configuration.integer;

        while (number > 99) {
            int remainder = number % 100;
            short twoDigits = UNSAFE.getShort(address + (remainder << 1));
            UNSAFE.putShort(offset, twoDigits); // + maximum boost
            offset += 2;
            number /= 100;
        }

        short twoDigits = UNSAFE.getShort(address + (number << 1));
        if (number > 9) {
            UNSAFE.putByte(offset++, (byte) (twoDigits));
        }

        UNSAFE.putByte(offset++, (byte) (twoDigits >> 8));
        return offset;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntegerFormatting.class.getSimpleName())
                .jvmArgsAppend(Configuration.JVM_ARGS)
                .build();

        new Runner(opt).run();
    }

}
