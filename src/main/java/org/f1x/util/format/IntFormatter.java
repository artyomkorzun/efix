package org.f1x.util.format;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.IntType;

import static org.f1x.util.format.FormatterUtil.digit;


public class IntFormatter {

    protected static final Buffer MIN_INT = BufferUtil.fromString(Integer.toString(Integer.MIN_VALUE));

    public static int formatInt(int value, MutableBuffer buffer, int offset) {
        if (value >= 0)
            return formatUInt(value, buffer, offset);
        else
            return formatNegativeInt(value, buffer, offset);
    }

    public static int formatNegativeInt(int value, MutableBuffer buffer, int offset) {
        if (value == Integer.MIN_VALUE) {
            buffer.putBytes(offset, MIN_INT);
            return offset + IntType.MAX_NEGATIVE_INT_LENGTH;
        }

        buffer.putByte(offset++, (byte) '-');
        return formatUInt(-value, buffer, offset);
    }

    public static int formatUInt(int value, MutableBuffer buffer, int offset) {
        int length = uintLength(value);
        int index = offset + length;
        formatUInt(value, buffer, offset, index);
        return index;
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

}
