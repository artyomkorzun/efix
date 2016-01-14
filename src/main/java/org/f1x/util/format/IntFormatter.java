package org.f1x.util.format;

import org.f1x.util.BufferUtil;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.FormatterUtil.digit;

@SuppressWarnings("Duplicates")
public class IntFormatter {

    protected static final Buffer MIN_INT = BufferUtil.fromString(Integer.MIN_VALUE + "");

    private static final int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

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

    public static void formatInt(int value, MutableBuffer buffer, MutableInt offset, int end) {
        if (value >= 0)
            formatUInt(value, buffer, offset, end);
        else
            formatNegativeInt(value, buffer, offset, end);
    }

    private static void formatNegativeInt(int value, MutableBuffer buffer, MutableInt offset, int end) {
        if (value == Integer.MIN_VALUE) {
            int off = offset.value();
            int length = MIN_INT.capacity();
            checkFreeSpace(end - off, length);

            buffer.putBytes(off, MIN_INT);
            offset.value(off + length);
            return;
        }

        int off = offset.value();
        value = -value;
        int length = uintLength(value) + 1;
        checkFreeSpace(end - off, length);

        formatUInt(value, off + length, buffer);
        buffer.putByte(off, (byte) '-');
        offset.value(off + length);
    }

    public static void formatUInt(int value, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        int length = uintLength(value);
        checkFreeSpace(end - off, length);

        int index = off + length;
        formatUInt(value, index, buffer);
        offset.value(off + length);
    }

    public static int uintLength(int x) {
        for (int i = 0; ; i++)
            if (x <= SIZE_TABLE[i])
                return i + 1;
    }

    protected static void formatUInt(int value, int index, MutableBuffer buffer) {
        int integer;
        int remainder;

        while (value > 0xFFFF) {
            integer = value / 100;
            remainder = value - ((integer << 6) + (integer << 5) + (integer << 2));
            buffer.putByte(--index, DIGIT_ONE[remainder]);
            buffer.putByte(--index, DIGIT_TEN[remainder]);
            value = integer;
        }

        do {
            integer = (value * 52429) >>> (16 + 3); // the same as value / 100
            remainder = value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, DIGIT[remainder]);
            value = integer;
        } while (value != 0);


        /*

        // TODO: replace

        div is replaced by inverse mul (3435973837 / 34359738368 = 0.10000000000582...)
        it works approx for value < 10^11 that covers all ints

        do {
            long integer = (value * 3435973837L) >>> (32 + 3);
            int remainder = (int) (value - ((integer << 3) + (integer << 1)));
            builder.append((char) (remainder + '0'));
            value = integer;
        } while (value != 0);

        */
    }

    // TODO: optimize
    protected static void format4DigitUInt(int value, MutableBuffer buffer, int offset) {
        for (int i = 3; i >= 0; i--) {
            buffer.putByte(offset + i, digit(value % 10));
            value /= 10;
        }
    }

    protected static void format3DigitUInt(int value, MutableBuffer buffer, int offset) {
        for (int i = 2; i >= 0; i--) {
            buffer.putByte(offset + i, digit(value % 10));
            value /= 10;
        }
    }

    protected static void format2DigitUInt(int value, MutableBuffer buffer, int offset) {
        for (int i = 1; i >= 0; i--) {
            buffer.putByte(offset + i, digit(value % 10));
            value /= 10;
        }
    }


}
