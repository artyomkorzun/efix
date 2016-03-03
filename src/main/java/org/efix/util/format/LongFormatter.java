package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.LongType;


public class LongFormatter {

    protected static final Buffer MIN_LONG = BufferUtil.fromString(Long.toString(Long.MIN_VALUE));

    private static final byte[] DIGIT_TEN = {
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

    private static final byte[] DIGIT_ONE = {
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

    public static int formatLong(long value, MutableBuffer buffer, int offset) {
        if (value >= 0)
            return formatULong(value, buffer, offset);
        else
            return formatNegativeLong(value, buffer, offset);
    }

    public static int formatNegativeLong(long value, MutableBuffer buffer, int offset) {
        if (value == Long.MIN_VALUE) {
            buffer.putBytes(offset, MIN_LONG);
            return offset + LongType.MAX_NEGATIVE_LONG_LENGTH;
        }

        buffer.putByte(offset++, (byte) '-');
        return formatULong(-value, buffer, offset);
    }


    public static int formatULong(long value, MutableBuffer buffer, int offset) {
        int length = ulongLength(value);
        int index = offset + length;
        formatULong(value, buffer, offset, index);
        return index;
    }

    protected static void formatULong(long value, MutableBuffer buffer, int offset, int index) {
        while (value > Integer.MAX_VALUE) {
            long integer = value / 100;
            int remainder = (int) (value - ((integer << 6) + (integer << 5) + (integer << 2)));
            buffer.putByte(--index, DIGIT_ONE[remainder]);
            buffer.putByte(--index, DIGIT_TEN[remainder]);
            value = integer;
        }

        IntFormatter.formatUInt((int) value, buffer, offset, index);
    }

    public static int ulongLength(long value) {
        if (value < 10L) return 1;
        if (value < 100L) return 2;
        if (value < 1000L) return 3;
        if (value < 10000L) return 4;
        if (value < 100000L) return 5;
        if (value < 1000000L) return 6;
        if (value < 10000000L) return 7;
        if (value < 100000000L) return 8;
        if (value < 1000000000L) return 9;
        if (value < 10000000000L) return 10;
        if (value < 100000000000L) return 11;
        if (value < 1000000000000L) return 12;
        if (value < 10000000000000L) return 13;
        if (value < 100000000000000L) return 14;
        if (value < 1000000000000000L) return 15;
        if (value < 10000000000000000L) return 16;
        if (value < 100000000000000000L) return 17;
        if (value < 1000000000000000000L) return 18;

        return 19;
    }

}
