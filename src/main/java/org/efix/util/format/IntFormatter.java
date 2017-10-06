package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.IntType;

import java.nio.ByteOrder;

import static org.efix.util.format.FormatterUtil.digit;


public class IntFormatter {

    protected static final Buffer MIN_INT = BufferUtil.fromString(Integer.toString(Integer.MIN_VALUE));

    public static int formatUIntInverse(int value, MutableBuffer buffer, int offset) {
        if (value > 99) {
            int newNumber = (int) (2748779070L * value >>> 38);
            int remainder = value - newNumber * 100;
            value = newNumber;

            offset -= 2;
            buffer.putShort(offset, NATIVE_DIGITS[remainder]);

            if (value > 99) {
                newNumber = (int) (2748779070L * value >>> 38);
                remainder = value - newNumber * 100;
                value = newNumber;

                offset -= 2;
                buffer.putShort(offset, NATIVE_DIGITS[remainder]);

                if (value > 99) {
                    newNumber = (int) (2748779070L * value >>> 38);
                    remainder = value - newNumber * 100;
                    value = newNumber;

                    offset -= 2;
                    buffer.putShort(offset, NATIVE_DIGITS[remainder]);

                    if (value > 99) {
                        newNumber = (41944 * value >>> 22);
                        remainder = value - newNumber * 100;
                        value = newNumber;

                        offset -= 2;
                        buffer.putShort(offset, NATIVE_DIGITS[remainder]);
                    }
                }
            }
        }

        short digits = LAST_DIGITS[value];
        if (value > 9) {
            buffer.putByte(--offset, (byte) (digits >> 8));
        }

        buffer.putByte(--offset, (byte) (digits));
        return offset;
    }


    public static int formatInt(int value, MutableBuffer buffer, int offset) {
        if (value >= 0)
            return formatUInt(value, buffer, offset);
        else
            return formatNegativeInt(value, buffer, offset);
    }

    public static int formatNegativeInt(int value, MutableBuffer buffer, int offset) {
        if (value == Integer.MIN_VALUE) {
            buffer.putBytes(offset, MIN_INT);
            return offset + IntType.MAX_LENGTH;
        }

        buffer.putByte(offset++, (byte) '-');
        return formatUInt(-value, buffer, offset);
    }

    public static int formatUInt(int value, MutableBuffer buffer, int offset) {
        return experimentalFormat(value, buffer, offset);
    }

    public static int format4DigitUInt(int value, MutableBuffer buffer, int offset) {
        return formatNDigitUShort(value, 4, buffer, offset);
    }

    public static int format3DigitUInt(int value, MutableBuffer buffer, int offset) {
        return formatNDigitUShort(value, 3, buffer, offset);
    }

    public static int format2DigitUInt(int value, MutableBuffer buffer, int offset) {
        return formatNDigitUShort(value, 2, buffer, offset);
    }

    protected static int formatNDigitUShort(int value, int length, MutableBuffer buffer, int offset) {
        int index = offset + length;
        formatUShort(value, buffer, offset, index);
        return index;
    }

    /**
     * Divs is replaced by inverse mul (3435973837 / 34359738368 = 0.10000000000582...).
     * It works for value < 5 * 10 ^ 9 that covers all ints.
     */
    protected static void formatUInt(int value, MutableBuffer buffer, int offset, int index) {
        while (value > 0xFFFF) {
            int integer = (int) ((value * 3435973837L) >>> (32 + 3));
            int remainder = value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, digit(remainder));
            value = integer;
        }

        formatUShort(value, buffer, offset, index);
    }

    /**
     * Div is replaced by inverse mul (52429 / 524288 = 0.100000381...).
     * It works for value < 8 * 10 ^ 4 that covers all shorts.
     */
    protected static void formatUShort(int value, MutableBuffer buffer, int offset, int index) {
        do {
            int integer = (value * 52429) >>> (16 + 3);
            int remainder = value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, digit(remainder));
            value = integer;
        } while (index > offset);
    }

    public static int uintLength(int value) {
        if (value < 10) return 1;
        if (value < 100) return 2;
        if (value < 1000) return 3;
        if (value < 10000) return 4;
        if (value < 100000) return 5;
        if (value < 1000000) return 6;
        if (value < 10000000) return 7;
        if (value < 100000000) return 8;
        if (value < 1000000000) return 9;

        return 10;
    }

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

    private static int experimentalFormat(int number, MutableBuffer buffer, int offset) {
        int length = uintLength(number);
        int end = offset + length;

        if (number > 99) {
            int newNumber = (int) (2748779070L * number >>> 38);
            int remainder = number - newNumber * 100;
            number = newNumber;

            buffer.putShort(end - 2, NATIVE_DIGITS[remainder]);

            if (number > 99) {
                newNumber = (int) (2748779070L * number >>> 38);
                remainder = number - newNumber * 100;
                number = newNumber;

                buffer.putShort(end - 4, NATIVE_DIGITS[remainder]);

                if (number > 99) {
                    newNumber = (int) (2748779070L * number >>> 38);
                    remainder = number - newNumber * 100;
                    number = newNumber;

                    buffer.putShort(end - 6, NATIVE_DIGITS[remainder]);

                    if (number > 99) {
                        newNumber = (41944 * number >>> 22);
                        remainder = number - newNumber * 100;
                        number = newNumber;

                        buffer.putShort(end - 8, NATIVE_DIGITS[remainder]);
                    }
                }
            }
        }

        short digits = LAST_DIGITS[number];
        if (number > 9) {
            buffer.putByte(offset + 1, (byte) (digits >> 8));
        }

        buffer.putByte(offset, (byte) (digits));
        return end;
    }

}
