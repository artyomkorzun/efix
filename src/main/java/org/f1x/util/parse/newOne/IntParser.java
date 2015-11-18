package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.*;

public class IntParser {

    protected static final int MAX_POSITIVE_LENGTH = 10;
    protected static final int MAX_NEGATIVE_LENGTH = MAX_POSITIVE_LENGTH + 1;

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        checkMinLength(end - off);

        byte b = buffer.getByte(off++);
        if (isDigit(b)) { // fast path
            long value = digit(b);

            do {
                b = buffer.getByte(off++);
                if (isDigit(b)) {
                    value = (value << 3) + (value << 1) + digit(b);
                } else if (b == separator) {
                    checkPositiveValue(value, off - start - 1);
                    offset.value(off);
                    return (int) value;
                } else {
                    throwInvalidChar(b);
                }
            } while (off < end);

        } else if (b == '-') {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                long value = digit(b);

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) + digit(b);
                    } else if (b == separator) {
                        value = -value;
                        checkNegativeValue(value, off - start - 1);
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

        checkMinLength(end - off);

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
                checkPositiveValue(value, off - start - 1);
                offset.value(off);
                return (int) value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static void checkPositiveValue(long value, int length) {
        if (length > MAX_POSITIVE_LENGTH | value > Integer.MAX_VALUE)
            throw new ParserException(String.format("number is too big, length %s", length));
    }

    protected static void checkNegativeValue(long value, int length) {
        if (length > MAX_NEGATIVE_LENGTH | value < Integer.MIN_VALUE)
            throw new ParserException(String.format("number is too small, length %s", length));
    }

}
