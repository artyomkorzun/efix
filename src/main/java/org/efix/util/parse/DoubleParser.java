package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DoubleType;

import static org.efix.util.parse.ParserUtil.*;


public class DoubleParser {

    private static final double[] INVERSE_POW_10 = {1E0, 1E-1, 1E-2, 1E-3, 1E-4, 1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11, 1E-12, 1E-13, 1E-14};

    public static double parseDouble(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, SIGN_LENGTH);

        if (buffer.getByte(off) == '-') {
            offset.set(off + SIGN_LENGTH);
            return -parseUDouble(separator, buffer, offset, end);
        } else {
            return parseUDouble(separator, buffer, offset, end);
        }
    }

    public static double parseUDouble(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkFreeSpace(end - off, DoubleType.MIN_LENGTH + SEPARATOR_LENGTH);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwInvalidChar(b);

        do {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == '.') {
                int fractionalOffset = off;

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) + digit(b);
                    } else if (b == separator) {
                        checkUDoubleLength(off - start - SEPARATOR_LENGTH, DoubleType.MAX_UDECIMAL_LENGTH);
                        offset.set(off);
                        return toDouble(value, off - fractionalOffset - SEPARATOR_LENGTH);
                    } else {
                        throwInvalidChar(b);
                    }
                }

            } else if (b == separator) {
                checkUDoubleLength(off - start - SEPARATOR_LENGTH, DoubleType.MAX_UINTEGER_LENGTH);
                offset.set(off);
                return value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static double toDouble(long value, int scale) {
        return value * INVERSE_POW_10[scale];
    }

    protected static void checkUDoubleLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("Decimal is too long, length %s, max length %s", length, max));
    }

}
