package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.parse.ParserUtil.*;


public class DecimalParser {

    public static long parseDecimal(int scale, byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(end - off, SIGN_LENGTH);

        if (buffer.getByte(off) == '-') {
            offset.set(off + SIGN_LENGTH);
            return -parseUDecimal(scale, separator, buffer, offset, end);
        } else {
            return parseUDecimal(scale, separator, buffer, offset, end);
        }
    }

    public static long parseUDecimal(int scale, byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkBounds(end - off, DecimalType.MIN_LENGTH + SEPARATOR_LENGTH);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwInvalidChar(b);

        do {
            b = buffer.getByte(off++);

            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == '.') {
                int integerLength = off - start - DOT_LENGTH;
                checkIntegerLength(integerLength, scale);
                start = off;

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) + digit(b);
                    } else if (b == separator) {
                        int fractionalLength = off - start - SEPARATOR_LENGTH;
                        checkFractionalLength(integerLength, fractionalLength, scale);
                        offset.set(off);
                        return value * DecimalType.multiplier(scale - fractionalLength);
                    } else {
                        throwInvalidChar(b);
                    }
                }

            } else if (b == separator) {
                checkIntegerLength(off - start - SEPARATOR_LENGTH, scale);
                offset.set(off);
                return value * DecimalType.multiplier(scale);
            } else {
                throwInvalidChar(b);
            }

        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static void checkIntegerLength(int length, int scale) {
        int max = Math.min(DecimalType.MAX_DIGITS, DecimalType.MAX_SCALE - scale);
        if (length > max)
            throw new ParserException(String.format("Integer part of decimal contains too many digits %s, max %s", length, max));
    }

    protected static void checkFractionalLength(int integerLength, int fractionalLength, int scale) {
        int max = Math.min(DecimalType.MAX_DIGITS - integerLength, scale);
        if (fractionalLength > max)
            throw new ParserException(String.format("Fractional part of decimal contains too many digits %s, max %s", fractionalLength, max));
    }

}
