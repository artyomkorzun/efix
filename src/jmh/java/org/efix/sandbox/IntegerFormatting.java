package org.efix.sandbox;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.efix.util.format.IntFormatter;
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
@Measurement(iterations = 5, time = 2)
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

    private static final UnsafeBuffer TWO_DIGITS_TABLE_BUFFER = UnsafeBuffer.allocateDirect(200);
    private static final long TWO_DIGITS_TABLE_ADDRESS;

    static {
        TWO_DIGITS_TABLE_ADDRESS = TWO_DIGITS_TABLE_BUFFER.addressOffset();
        for (int i = 0; i < TWO_DIGITS.length; i++) {
            TWO_DIGITS_TABLE_BUFFER.putShort(i << 1, TWO_DIGITS[i]);
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int baseline() {
        return Config.randomInt();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy10WithArray() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        while (number > 9) {
            int newNumber = (int) (number * 3435973837L >>> 35);
            int remainder = number - (newNumber << 3) - (newNumber << 1);
            number = newNumber;
            array[offset++] = (byte) ('0' + remainder);
        }

        array[offset++] = (byte) ('0' + number);
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy10WithUnrolledArray() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();
        int remainder;
        int newNumber;

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = (int) (number * 3435973837L >>> 35);
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = number * 52429 >>> 19;
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = number * 52429 >>> 19;
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        newNumber = number * 52429 >>> 19;
        remainder = number - (newNumber << 3) - (newNumber << 1);
        number = newNumber;
        array[offset++] = (byte) ('0' + remainder);
        if (number == 0) {
            return offset;
        }

        array[offset++] = (byte) ('0' + number);
        return offset;
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithArray() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        while (number > 99) {
            int newNumber = (int) (1374389535L * number >>> 37);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short twoDigits = TWO_DIGITS[remainder];
            array[offset++] = (byte) (twoDigits);
            array[offset++] = (byte) (twoDigits >> 8);
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            array[offset++] = (byte) (twoDigits);
        }

        array[offset++] = (byte) (twoDigits >> 8);
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int divBy100WithUnsafeStoreToArray() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        while (number > 99) {
            int newNumber = (int) (1374389535L * number >>> 37);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short twoDigits = TWO_DIGITS[remainder];
            //short twoDigits = UNSAFE.getShort(TWO_DIGITS_TABLE_ADDRESS + (remainder << 1));
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, twoDigits); // + maximum boost
            offset += 2;
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset++, (byte) (twoDigits));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset++, (byte) (twoDigits >> 8));
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeStoreToArrayDirect() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        int length = integerLengthByCascadingIf(number);
        int end = offset + length;
        offset = end;

        while (number > 99) {
            int newNumber = (int) (1374389535L * number >>> 37);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short twoDigits = TWO_DIGITS[remainder];
            offset -= 2;
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, twoDigits); // + maximum boost
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + --offset, (byte) (twoDigits));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset - 1, (byte) (twoDigits >> 8));
        return end;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public long divBy100WithUnsafeStoreToMemory() {
        Buffer buffer = Config.buffer;
        long offset = buffer.addressOffset() + Config.offset;
        int number = Config.randomInt();

        while (number > 99) {
            int newNumber = (int) (1374389535L * number >>> 37);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short twoDigits = TWO_DIGITS[remainder];
            UNSAFE.putShort(offset, twoDigits); // + maximum boost
            offset += 2;
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(offset++, (byte) (twoDigits));
        }

        UNSAFE.putByte(offset++, (byte) (twoDigits >> 8));
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithBuffer() {
        MutableBuffer buffer = Config.buffer;
        int offset = Config.offset;
        int number = Config.randomInt();

        while (number > 99) {
            int newNumber = (int) (1374389535L * number >>> 37);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short twoDigits = TWO_DIGITS[remainder];
            buffer.putShort(offset, twoDigits); // + maximum boost
            offset += 2;
        }

        short twoDigits = TWO_DIGITS[number];
        if (number > 9) {
            buffer.putByte(offset++, (byte) (twoDigits));
        }

        buffer.putByte(offset++, (byte) (twoDigits >> 8));
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int currentImplementation() {
        MutableBuffer buffer = Config.buffer;
        int offset = Config.offset;
        int number = Config.randomInt();
        return IntFormatter.formatUInt(number, buffer, offset);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int integerLengthWithCascadingIf() {
        int integer = Config.randomInt();
        return integerLengthByCascadingIf(integer);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int integerLengthWithLoop() {
        int integer = Config.randomInt();
        return integerLengthByLoop(integer);
    }

    public static int integerLengthByCascadingIf(int value) {
        if (value < 10)
            return 1;
        if (value < 100)
            return 2;
        if (value < 1000)
            return 3;
        if (value < 10000)
            return 4;
        if (value < 100000)
            return 5;
        if (value < 1000000)
            return 6;
        if (value < 10000000)
            return 7;
        if (value < 100000000)
            return 8;
        if (value < 1000000000)
            return 9;

        return 10;
    }

    private static final int[] SIZES = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};

    public static int integerLengthByLoop(int value) {
        for (int i = 1; i < SIZES.length; i++) {
            if (value < SIZES[i]) {
                return i;
            }
        }

        return SIZES.length;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntegerFormatting.class.getSimpleName())
                .jvmArgsAppend(Config.JVM_ARGS)
                .build();

        new Runner(opt).run();
    }

}
