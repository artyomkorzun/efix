package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.*;

public class IntParser {

    public static final int MAX_POSITIVE_INT_LENGTH = 10;
    public static final int MAX_NEGATIVE_INT_LENGTH = 11;
    public static final int MIN_SIZE = 2;

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        ensureSize(MIN_SIZE, off, end);

        byte b = buffer.getByte(off++);
        if (isDigit(b)) { // fast path
            long value = digit(b);

            do {
                b = buffer.getByte(off++);
                if (isDigit(b)) {
                    value = (value << 3) + (value << 1) + digit(b);
                } else if (b == separator) {
                    int length = off - start - 1;
                    checkPositiveValue(value, length);
                    offset.value(off);
                    return (int) value;
                } else {
                    throwInvalidChar(b);
                }
            } while (off < end);

        } else if (b == '-') {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                long value = -digit(b);

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) - digit(b);
                    } else if (b == separator) {
                        int length = off - start - 1;
                        checkNegativeValue(value, length);
                        offset.value(off);
                        return (int) value;
                    } else {
                        throwInvalidChar(b);
                    }
                }

            } else {
                throwInvalidChar(b);
            }
        } else {
            throwInvalidChar(b);
        }

        throw throwSeparatorNotFound(separator);
    }

    public static int parsePositiveInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        ensureSize(MIN_SIZE, off, end);

        long value = 0;
        byte b = buffer.getByte(off++);
        if (isDigit(b))
            value = digit(b);
        else
            throwInvalidChar(b);

        do {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == separator) {
                int length = off - start - 1;
                checkPositiveValue(value, length);
                offset.value(off);
                return (int) value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

       throw throwSeparatorNotFound(separator);
    }

    private static void checkPositiveValue(long value, int length) {
        if (length > MAX_POSITIVE_INT_LENGTH | value > Integer.MAX_VALUE)
            throw new ParserException(String.format("Integer %s is too big, length %s", value, length));
    }

    private static void checkNegativeValue(long value, int length) {
        if (length > MAX_NEGATIVE_INT_LENGTH | value < Integer.MIN_VALUE)
            throw new ParserException(String.format("Integer %s is too small, length %s", value, length));
    }

}
