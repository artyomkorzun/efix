package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.IntType;

import static org.efix.util.parse.ParserUtil.*;


public class IntParser {

    public static int parseInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(SIGN_LENGTH, end - off);

        if (buffer.getByte(off) == '-') {
            offset.set(off + SIGN_LENGTH);
            return -parseUInt(separator, buffer, offset, end);
        } else {
            return parseUInt(separator, buffer, offset, end);
        }
    }

    public static int parseUInt(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkBounds(IntType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwUnexpectedByte(b);

        do {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == separator) {
                int digits = off - start - SEPARATOR_LENGTH;
                checkUInt(value, digits);
                offset.set(off);
                return (int) value;
            } else {
                throwUnexpectedByte(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
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

    protected static void checkUInt(long value, int digits) {
        if (digits > IntType.MAX_UINT_LENGTH || value > Integer.MAX_VALUE)
            throw new ParserException(String.format("Integer is too big, digits %s", digits));
    }

}
