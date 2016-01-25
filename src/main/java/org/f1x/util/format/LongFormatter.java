package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.FormatterUtil.checkFreeSpace;

@SuppressWarnings("Duplicates")
public class LongFormatter {

    protected static final Buffer MIN_LONG = BufferUtil.fromString(Long.MIN_VALUE + "");

    private static final long[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999,
            999999999, 9999999999L, 99999999999L, 999999999999L, 9999999999999L, 99999999999999L,
            999999999999999L, 9999999999999999L, 99999999999999999L, 999999999999999999L, Long.MAX_VALUE
    };

    private static final byte[] DIGIT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

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

    public static void formatLong(long value, MutableBuffer buffer, MutableInt offset, int end) {
        if (value >= 0)
            formatULong(value, buffer, offset, end);
        else
            formatNegativeLong(value, buffer, offset, end);
    }

    public static void formatULong(long value, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        int length = ulongLength(value);
        checkFreeSpace(end - off, length);

        int index = off + length;
        formatULong(value, index, buffer);
        offset.set(off + length);
    }

    public static void formatNegativeLong(long value, MutableBuffer buffer, MutableInt offset, int end) {
        if (value == Long.MIN_VALUE) {
            int off = offset.get();
            int length = MIN_LONG.capacity();
            checkFreeSpace(end - off, length);

            buffer.putBytes(off, MIN_LONG);
            offset.set(off + length);
            return;
        }

        int off = offset.get();
        value = -value;
        int length = ulongLength(value) + 1;
        checkFreeSpace(end - off, length);

        formatULong(value, off + length, buffer);
        buffer.putByte(off, (byte) '-');
        offset.set(off + length);
    }

    public static int ulongLength(long x) {
        for (int i = 0; ; i++)
            if (x <= SIZE_TABLE[i])
                return i + 1;
    }

    protected static void formatULong(long value, int index, MutableBuffer buffer) {
        long integer;
        int remainder;

        while (value > 0xFFFF) {
            integer = value / 100;
            remainder = (int) (value - ((integer << 6) + (integer << 5) + (integer << 2)));
            buffer.putByte(--index, DIGIT_ONE[remainder]);
            buffer.putByte(--index, DIGIT_TEN[remainder]);
            value = integer;
        }

        do {
            integer = (value * 52429) >>> (16 + 3); // the same as value / 100
            remainder = (int) (value - ((integer << 3) + (integer << 1)));
            buffer.putByte(--index, DIGIT[remainder]);
            value = integer;
        } while (value != 0);
    }

}
