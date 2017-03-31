package org.efix.sandbox;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import static org.efix.util.UnsafeAccess.UNSAFE;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 2)
public class IntegerFormatting {

   /* private static final byte[] FIRST_DIGIT = {
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
    };*/

    private static final short[] LITTLE_ENDIAN_TWO_DIGITS;
    private static final short[] NATIVE_ENDIAN_TWO_DIGITS;

    static {
        short[] littleEndianTable = new short[100];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                littleEndianTable[i * 10 + j] = (short) ((('0' + j) << 8) + ('0' + i));
            }
        }

        short[] nativeEndianTable = littleEndianTable;
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            nativeEndianTable = new short[100];

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    nativeEndianTable[i * 10 + j] = (short) ((('0' + i) << 8) + ('0' + j));
                }
            }
        }

        LITTLE_ENDIAN_TWO_DIGITS = littleEndianTable;
        NATIVE_ENDIAN_TWO_DIGITS = nativeEndianTable;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int baseline() {
        return Config.randomInt();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeStoreToArrayWithLoop() {
        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        int length = integerLengthByCascadingIf(number);
        int end = offset + length;
        offset = end;

        while (number > 99) {
            int newNumber = (int) (2748779070L * number >>> 38);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short digits = NATIVE_ENDIAN_TWO_DIGITS[remainder];
            offset -= 2;
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, digits);
        }

        short twoDigits = LITTLE_ENDIAN_TWO_DIGITS[number];
        if (number > 9) {
            offset--;
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, (byte) (twoDigits >>> 8));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset - 1, (byte) (twoDigits));
        return end;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeStoreToArrayWithNestedIfs() {
        /**
         * Implementation details:
         * 1) Find 2 digits at a time.
         * 2) Store 2 digits at a time.
         * 3) Unroll loop to eliminate safe point check and another stuff in loop body (It has <= 4 iterations only)
         * 4) Substitute division by inverse multiplication (Compiler does that but adds one more mov instruction)
         * 5) Use unsafe store to array to remove bounds check
         */

        byte[] array = Config.array;
        int offset = Config.offset;
        int number = Config.randomInt();

        int length = integerLengthByCascadingIf(number);
        int end = offset + length;

        if (number > 99) {
            int newNumber = (int) (2748779070L * number >>> 38);
            int remainder = number - newNumber * 100;
            number = newNumber;

            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 2, NATIVE_ENDIAN_TWO_DIGITS[remainder]);

            if (number > 99) {
                newNumber = (int) (2748779070L * number >>> 38);
                remainder = number - newNumber * 100;
                number = newNumber;

                UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 4, NATIVE_ENDIAN_TWO_DIGITS[remainder]);

                if (number > 99) {
                    newNumber = (int) (2748779070L * number >>> 38);
                    remainder = number - newNumber * 100;
                    number = newNumber;

                    UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 6, NATIVE_ENDIAN_TWO_DIGITS[remainder]);

                    if (number > 99) {
                        newNumber = (41944 * number >>> 22);
                        remainder = number - newNumber * 100;
                        number = newNumber;

                        UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 8, NATIVE_ENDIAN_TWO_DIGITS[remainder]);
                    }
                }
            }
        }

        short digits = LITTLE_ENDIAN_TWO_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset + 1, (byte) (digits >>> 8));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, (byte) digits);
        return end;
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

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntegerFormatting.class.getSimpleName())
                .jvmArgsAppend(Config.JVM_ARGS)
                .build();

        new Runner(opt).run();
    }

}
