package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.IntType;

@SuppressWarnings("Duplicates")
public class IntParser {

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        ParserUtil.checkFreeSpace(end - off, IntType.MIN_LENGTH + 1);

        byte b = buffer.getByte(off++);
        if (ParserUtil.isDigit(b)) { // fast path
            long value = ParserUtil.digit(b);

            do {
                b = buffer.getByte(off++);
                if (ParserUtil.isDigit(b)) {
                    value = (value << 3) + (value << 1) + ParserUtil.digit(b);
                } else if (b == separator) {
                    checkUnsignedValue(value, off - start - 1);
                    offset.set(off);
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
                        offset.set(off);
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

    public static int parseUInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        ParserUtil.checkFreeSpace(end - off, IntType.MIN_LENGTH + 1);

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
                checkUnsignedValue(value, off - start - 1);
                offset.set(off);
                return (int) value;
            } else {
                ParserUtil.throwInvalidChar(b);
            }
        } while (off < end);

        throw ParserUtil.throwSeparatorNotFound(separator);
    }

    protected static int parse2DigitUInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        return value;
    }

    protected static int parse3DigitUInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 2));
        return value;
    }

    protected static int parse4DigitUInt(Buffer buffer, int offset) {
        int value = ParserUtil.checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 2));
        value = (value << 3) + (value << 1) + ParserUtil.checkDigit(buffer.getByte(offset + 3));
        return value;
    }

    protected static void checkUnsignedValue(long value, int length) {
        if (length > IntType.MAX_UNSIGNED_INT_LENGTH || value > Integer.MAX_VALUE)
            throw new ParserException(String.format("number is too big, length %s", length));
    }

    protected static void checkNegativeValue(long value, int length) {
        if (length > IntType.MAX_NEGATIVE_INT_LENGTH || value < Integer.MIN_VALUE)
            throw new ParserException(String.format("number is too small, length %s", length));
    }

}
