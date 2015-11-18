package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.*;

public class LongParser {

    protected static final int MAX_POSITIVE_LENGTH = 18;
    protected static final int MAX_NEGATIVE_LENGTH = MAX_POSITIVE_LENGTH + 1;

    public static long parseLong(byte separator, Buffer buffer, MutableInt offset, int end) {
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
                    checkValueLength(off - start - 1, MAX_POSITIVE_LENGTH);
                    offset.value(off);
                    return value;
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
                        checkValueLength(off - start - 1, MAX_NEGATIVE_LENGTH);
                        offset.value(off);
                        return -value;
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

    public static long parsePositiveLong(byte separator, Buffer buffer, MutableInt offset, int end) {
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
                checkValueLength(off - start - 1, MAX_POSITIVE_LENGTH);
                offset.value(off);
                return value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static void checkValueLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("number is too long, length %s, max %s", length, max));
    }

}
