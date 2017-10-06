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

    private static final short[] NATIVE_DIGITS;
    private static final short[] LAST_DIGITS;

    static {
        short[] nativeDigits = new short[100];

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    nativeDigits[i * 10 + j] = (short) ((('0' + j) << 8) + ('0' + i));
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    nativeDigits[i * 10 + j] = (short) ((('0' + i) << 8) + ('0' + j));
                }
            }
        }

        short[] lastDigits = new short[100];

        for (int i = 0; i < 10; i++) {
            lastDigits[i] = (short) ((('0' + i) << 8) + ('0' + i));
        }

        for (int i = 1; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                lastDigits[i * 10 + j] = (short) ((('0' + j) << 8) + ('0' + i));
            }
        }

        NATIVE_DIGITS = nativeDigits;
        LAST_DIGITS = lastDigits;
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

            short digits = NATIVE_DIGITS[remainder];
            offset -= 2;
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, digits);
        }

        short digits = LAST_DIGITS[number];
        if (number > 9) {
            offset--;
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, (byte) (digits));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset - 1, (byte) (digits >>> 8));
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

            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 2, NATIVE_DIGITS[remainder]);

            if (number > 99) {
                newNumber = (int) (2748779070L * number >>> 38);
                remainder = number - newNumber * 100;
                number = newNumber;

                UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 4, NATIVE_DIGITS[remainder]);

                if (number > 99) {
                    newNumber = (int) (2748779070L * number >>> 38);
                    remainder = number - newNumber * 100;
                    number = newNumber;

                    UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 6, NATIVE_DIGITS[remainder]);

                    if (number > 99) {
                        newNumber = (41944 * number >>> 22);
                        remainder = number - newNumber * 100;
                        number = newNumber;

                        UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + end - 8, NATIVE_DIGITS[remainder]);
                    }
                }
            }
        }

        short digits = LAST_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset + 1, (byte) (digits >>> 8));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, (byte) (digits));
        return end;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeStoreToArrayInverse() {
        /**
         * Implementation details:
         * 1) Find 2 digits at a time.
         * 2) Store 2 digits at a time.
         * 3) Unroll loop to eliminate safe point check and another stuff in loop body (It has <= 4 iterations only)
         * 4) Substitute division by inverse multiplication (Compiler does that but adds one more mov instruction)
         * 5) Use unsafe store to array to remove bounds check
         */

        byte[] array = Config.array;
        int offset = Config.length;
        int number = Config.randomInt();

        if (number > 99) {
            int newNumber = (int) (2748779070L * number >>> 38);
            int remainder = number - newNumber * 100;
            number = newNumber;

            offset -= 2;
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, NATIVE_DIGITS[remainder]);

            if (number > 99) {
                newNumber = (int) (2748779070L * number >>> 38);
                remainder = number - newNumber * 100;
                number = newNumber;

                offset -= 2;
                UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, NATIVE_DIGITS[remainder]);

                if (number > 99) {
                    newNumber = (int) (2748779070L * number >>> 38);
                    remainder = number - newNumber * 100;
                    number = newNumber;

                    offset -= 2;
                    UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, NATIVE_DIGITS[remainder]);

                    if (number > 99) {
                        newNumber = (41944 * number >>> 22);
                        remainder = number - newNumber * 100;
                        number = newNumber;

                        offset -= 2;
                        UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, NATIVE_DIGITS[remainder]);
                    }
                }
            }
        }

        short digits = LAST_DIGITS[number];
        if (number > 9) {
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + --offset, (byte) (digits >>> 8));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + --offset, (byte) (digits));
        return offset;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int divBy100WithUnsafeStoreToArrayINverseWithLoop() {
        byte[] array = Config.array;
        int offset = Config.length;
        int number = Config.randomInt();

        for (int i = 0; i < 4; i++) {
            if (number <= 99) {
                break;
            }

            int newNumber = (int) (2748779070L * number >>> 38);
            int remainder = number - newNumber * 100;
            number = newNumber;

            short digits = NATIVE_DIGITS[remainder];
            offset -= 2;
            UNSAFE.putShort(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, digits);
        }

        short digits = LAST_DIGITS[number];
        if (number > 9) {
            offset--;
            UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + --offset, (byte) (digits));
        }

        UNSAFE.putByte(array, Unsafe.ARRAY_BYTE_BASE_OFFSET + --offset, (byte) (digits >>> 8));
        return offset;
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
