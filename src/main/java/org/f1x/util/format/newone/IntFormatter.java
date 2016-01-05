package org.f1x.util.format.newone;

import org.f1x.util.BufferUtil;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.newone.FormatterUtil.checkFreeSpace;

@SuppressWarnings("Duplicates")
public class IntFormatter {

    protected static final byte SIGN_BYTE = '-';
    protected static final Buffer MIN_INT = BufferUtil.create(Integer.MIN_VALUE + "");

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
        if (value >= 0) {
            formatPositiveInt(value, buffer, offset, end);
        } else {
            int off = offset.value();

            if (value == Integer.MIN_VALUE) {
                int length = MIN_INT.capacity();
                checkFreeSpace(end - off, length);

                buffer.putBytes(off, MIN_INT);
                offset.value(off + length);
                return;
            }

            value = -value;
            int length = positiveIntLength(value) + 1;
            checkFreeSpace(end - off, length);

            formatPositiveInt(value, off + length, buffer);
            buffer.putByte(off, SIGN_BYTE);
            offset.value(off + length);
        }
    }

    public static void formatPositiveInt(int value, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        int length = positiveIntLength(value);
        checkFreeSpace(end - off, length);

        int index = off + length;
        formatPositiveInt(value, index, buffer);
        offset.value(off + length);
    }

    public static int positiveIntLength(int x) {
        for (int i = 0; ; i++)
            if (x <= SIZE_TABLE[i])
                return i + 1;
    }

    protected static void formatPositiveInt(int value, int index, MutableBuffer buffer) {
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
            integer = (value * 52429) >>> (16 + 3); // inverse division, the same as value / 100
            remainder = value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, DIGIT[remainder]);
            value = integer;
        } while (value != 0);
    }

}
