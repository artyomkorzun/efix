package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.LongType;

import static org.efix.util.format.IntFormatter.DIGITS;


public class LongFormatter {

    protected static final Buffer MIN_LONG = BufferUtil.fromString(Long.toString(Long.MIN_VALUE));

    public static int formatLong(long value, MutableBuffer buffer, int offset) {
        if (value < 0) {
            if (value == Long.MIN_VALUE) {
                buffer.putBytes(offset, MIN_LONG);
                return offset + LongType.MAX_LENGTH;
            }

            buffer.putByte(offset++, (byte) '-');
            value = -value;
        }

        return formatULong(value, buffer, offset);
    }

    public static int formatULong(long value, MutableBuffer buffer, int offset) {
        int length = ulongLength(value);
        int index = offset + length;
        formatULong(value, buffer, offset, index);
        return index;
    }

    protected static void formatULong(long value, MutableBuffer buffer, int offset, int index) {
        if (value > Integer.MAX_VALUE) {
            value = format10ULong(value, buffer, index);
            index -= 10;
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

    private static long format10ULong(long value, MutableBuffer buffer, int index) {
        for (int i = 0; i < 5; i++) {
            final long newValue = value / 100;
            final int remainder = (int) (value - (newValue * 100));
            value = newValue;

            buffer.putShort(index - 2 - (i << 1), DIGITS[remainder]);
        }

        return value;
    }

}
