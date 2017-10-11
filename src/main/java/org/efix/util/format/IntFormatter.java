package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.IntType;

import java.nio.ByteOrder;

import static org.efix.util.format.FormatterUtil.digit;


public class IntFormatter {

    protected static final Buffer MIN_INT = BufferUtil.fromString(Integer.toString(Integer.MIN_VALUE));
    protected static final short[] DIGITS = makeDigits();

    public static int formatInt(int value, MutableBuffer buffer, int offset) {
        if (value < 0) {
            if (value == Integer.MIN_VALUE) {
                buffer.putBytes(offset, MIN_INT);
                return offset + IntType.MAX_LENGTH;
            }

            buffer.putByte(offset++, (byte) '-');
            value = -value;
        }

        return formatUInt(value, buffer, offset);
    }

    public static int formatUInt(int value, MutableBuffer buffer, int offset) {
        final int end = offset + uintLength(value);
        int index = end;

        while (value > 9) {
            final int newValue = (int) (2748779070L * value >>> 38);
            final int remainder = value - newValue * 100;
            value = newValue;

            index -= 2;
            buffer.putShort(index, DIGITS[remainder]);
        }

        if (offset < index) {
            buffer.putByte(offset, (byte) (value + '0'));
        }

        return end;
    }

    public static int format4DigitUInt(int value, MutableBuffer buffer, int offset) {
        final int firstDigits = (int) (2748779070L * value >>> 38);
        final int lastDigits = value - firstDigits * 100;

        buffer.putShort(offset, DIGITS[firstDigits]);
        buffer.putShort(offset + 2, DIGITS[lastDigits]);

        return offset + 4;
    }

    public static int format3DigitUInt(int value, MutableBuffer buffer, int offset) {
        final int firstDigit = (int) (2748779070L * value >>> 38);
        final int lastDigits = value - firstDigit * 100;

        buffer.putByte(offset, (byte) (firstDigit + '0'));
        buffer.putShort(offset + 1, DIGITS[lastDigits]);

        return offset + 3;
    }

    public static int format2DigitUInt(int value, MutableBuffer buffer, int offset) {
        buffer.putShort(offset, DIGITS[value]);
        return offset + 2;
    }

    protected static int formatNDigitUInt(int value, int length, MutableBuffer buffer, int offset) {
        int index = offset + length;

        while (index > offset) {
            int integer = (int) ((value * 3435973837L) >>> (32 + 3));
            int remainder = value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, digit(remainder));
            value = integer;
        }

        return offset + length;
    }

    protected static void formatUInt(int value, MutableBuffer buffer, int offset, int index) {
        final int end = offset + 1;

        while (end < index) {
            final int newValue = (int) (2748779070L * value >>> 38);
            final int remainder = value - newValue * 100;
            value = newValue;

            index -= 2;
            buffer.putShort(index, DIGITS[remainder]);
        }

        if (offset < index) {
            buffer.putByte(offset, (byte) (value + '0'));
        }
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

    private static short[] makeDigits() {
        short[] digits = new short[100];

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    digits[i * 10 + j] = (short) ((('0' + j) << 8) + ('0' + i));
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    digits[i * 10 + j] = (short) ((('0' + i) << 8) + ('0' + j));
                }
            }
        }

        return digits;
    }

}
