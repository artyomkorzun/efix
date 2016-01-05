package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.ParserUtil.*;

@SuppressWarnings("Duplicates")
public class IntParser {

    protected static final int MAX_UNSIGNED_INT_LENGTH = 10;
    protected static final int MAX_NEGATIVE_INT_LENGTH = MAX_UNSIGNED_INT_LENGTH + 1;

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        checkFreeSpace(end - off, MIN_LENGTH);

        byte b = buffer.getByte(off++);
        if (isDigit(b)) { // fast path
            long value = digit(b);

            do {
                b = buffer.getByte(off++);
                if (isDigit(b)) {
                    value = (value << 3) + (value << 1) + digit(b);
                } else if (b == separator) {
                    checkUnsignedValue(value, off - start - 1);
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

    public static int parseUInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        checkFreeSpace(end - off, MIN_LENGTH);

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
                checkUnsignedValue(value, off - start - 1);
                offset.value(off);
                return (int) value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static int parse2DigitUInt(Buffer buffer, int offset) {
        int value = checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 1));
        return value;
    }

    protected static int parse3DigitUInt(Buffer buffer, int offset) {
        int value = checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 2));
        return value;
    }

    protected static int parse4DigitUInt(Buffer buffer, int offset) {
        int value = checkDigit(buffer.getByte(offset));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 1));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 2));
        value = (value << 3) + (value << 1) + checkDigit(buffer.getByte(offset + 3));
        return value;
    }

    protected static void checkUnsignedValue(long value, int length) {
        if (length > MAX_UNSIGNED_INT_LENGTH || value > Integer.MAX_VALUE)
            throw new ParserException(String.format("number is too big, length %s", length));
    }

    protected static void checkNegativeValue(long value, int length) {
        if (length > MAX_NEGATIVE_INT_LENGTH || value < Integer.MIN_VALUE)
            throw new ParserException(String.format("number is too small, length %s", length));
    }

}
