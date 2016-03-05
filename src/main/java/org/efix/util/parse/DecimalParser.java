package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.parse.ParserUtil.*;


@SuppressWarnings("Duplicates")
public class DecimalParser {

    public static final int MAX_UNSIGNED_INTEGER_LENGTH = DecimalType.MAX_UNSIGNED_INTEGER_LENGTH - 1;
    public static final int MAX_NEGATIVE_INTEGER_LENGTH = DecimalType.MAX_NEGATIVE_INTEGER_LENGTH - 1;

    public static long parseDecimal(int scale, byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkFreeSpace(end - off, DecimalType.MIN_LENGTH + SEPARATOR_LENGTH);

        byte b = buffer.getByte(off++);
        if (isDigit(b)) { // fast path
            long value = digit(b);

            do {
                b = buffer.getByte(off++);
                if (isDigit(b)) {
                    value = (value << 3) + (value << 1) + digit(b);
                } else if (b == '.') {
                    checkIntegerPartLength(off - start - 1, MAX_UNSIGNED_INTEGER_LENGTH - scale);
                    start = off;

                    while (off < end) {
                        b = buffer.getByte(off++);
                        if (isDigit(b)) {
                            value = (value << 3) + (value << 1) + digit(b);
                        } else if (b == separator) {
                            int fractions = off - start - SEPARATOR_LENGTH;
                            checkFractionalPartLength(fractions, scale);
                            offset.set(off);
                            return value * DecimalType.multiplier(scale - fractions);
                        } else {
                            throwInvalidChar(b);
                        }
                    }

                } else if (b == separator) {
                    checkIntegerPartLength(off - start - SEPARATOR_LENGTH, MAX_UNSIGNED_INTEGER_LENGTH - scale);
                    offset.set(off);
                    return value * DecimalType.multiplier(scale);
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
                    } else if (b == '.') {
                        checkIntegerPartLength(off - start - SEPARATOR_LENGTH, MAX_NEGATIVE_INTEGER_LENGTH - scale);
                        start = off;

                        while (off < end) {
                            b = buffer.getByte(off++);
                            if (isDigit(b)) {
                                value = (value << 3) + (value << 1) + digit(b);
                            } else if (b == separator) {
                                int fractions = off - start - 1;
                                checkFractionalPartLength(fractions, scale);
                                offset.set(off);
                                return -(value * DecimalType.multiplier(scale - fractions));
                            } else {
                                throwInvalidChar(b);
                            }
                        }

                    } else if (b == separator) {
                        checkIntegerPartLength(off - start - SEPARATOR_LENGTH, MAX_NEGATIVE_INTEGER_LENGTH - scale);
                        offset.set(off);
                        return -(value * DecimalType.multiplier(scale));
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

    protected static void checkIntegerPartLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("Integer part of decimal is too long, length %s, max length %s", length, max));
    }

    protected static void checkFractionalPartLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("Fractional part of decimal is too long, length %s, max length %s", length, max));
    }

}
