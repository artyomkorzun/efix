package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

public class IntParser {

    protected static final int MAX_POSITIVE_LENGTH = 10;
    protected static final int MAX_NEGATIVE_LENGTH = MAX_POSITIVE_LENGTH + 1;

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
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
                    checkPositiveValue(value, off - start - 1);
                    offset.value(off);
                    return (int) value;
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
                        value = -value;
                        checkNegativeValue(value, off - start - 1);
                        offset.value(off);
                        return (int) value;
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

    public static int parsePositiveInt(byte separator, Buffer buffer, MutableInt offset, int end) {
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
                checkPositiveValue(value, off - start - 1);
                offset.value(off);
                return (int) value;
            } else {
                ParserUtil.throwInvalidChar(b);
            }
        } while (off < end);

        throw ParserUtil.throwSeparatorNotFound(separator);
    }

    protected static int parse2DigitInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        return value;
    }

    protected static int parse3DigitInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 2));
        return value;
    }

    protected static int parse4DigitInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 2));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 3));
        return value;
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
