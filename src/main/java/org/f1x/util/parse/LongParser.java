package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

public class LongParser {

    protected static final int MAX_POSITIVE_LENGTH = 18;
    protected static final int MAX_NEGATIVE_LENGTH = MAX_POSITIVE_LENGTH + 1;

    public static long parseLong(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        ParserUtil.checkMinLength(end - off, ParserUtil.MIN_LENGTH);

        byte b = buffer.getByte(off++);
        if (ParserUtil.isDigit(b)) { // fast path
            long value = ParserUtil.digit(b);

            do {
                b = buffer.getByte(off++);
                if (ParserUtil.isDigit(b)) {
                    value = (value << 3) + (value << 1) + ParserUtil.digit(b);
                } else if (b == separator) {
                    checkValueLength(off - start - 1, MAX_POSITIVE_LENGTH);
                    offset.value(off);
                    return value;
                } else {
                    ParserUtil.throwInvalidChar(b);
                }
            } while (off < end);

        } else if (b == '-') {
            b = buffer.getByte(off++);
            if (ParserUtil.isDigit(b)) {
                long value = ParserUtil.digit(b);

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (ParserUtil.isDigit(b)) {
                        value = (value << 3) + (value << 1) + ParserUtil.digit(b);
                    } else if (b == separator) {
                        checkValueLength(off - start - 1, MAX_NEGATIVE_LENGTH);
                        offset.value(off);
                        return -value;
                    } else {
                        ParserUtil.throwInvalidChar(b);
                    }
                }

            } else {
                ParserUtil.throwInvalidChar(b);
            }
        } else {
            ParserUtil.throwInvalidChar(b);
        }

        throw ParserUtil.throwSeparatorNotFound(separator);
    }

    public static long parsePositiveLong(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        ParserUtil.checkMinLength(end - off, ParserUtil.MIN_LENGTH);

        long value = 0;
        byte b = buffer.getByte(off++);
        if (ParserUtil.isDigit(b))
            value = ParserUtil.digit(b);
        else
            ParserUtil.throwInvalidChar(b);

        do {
            b = buffer.getByte(off++);
            if (ParserUtil.isDigit(b)) {
                value = (value << 3) + (value << 1) + ParserUtil.digit(b);
            } else if (b == separator) {
                checkValueLength(off - start - 1, MAX_POSITIVE_LENGTH);
                offset.value(off);
                return value;
            } else {
                ParserUtil.throwInvalidChar(b);
            }
        } while (off < end);

        throw ParserUtil.throwSeparatorNotFound(separator);
    }

    protected static void checkValueLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("number is too long, length %s, max %s", length, max));
    }

}
