package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.*;

public class DoubleParser {

    protected static final int MAX_POSITIVE_INTEGER_LENGTH = 15;
    protected static final int MAX_NEGATIVE_INTEGER_LENGTH = 16;
    protected static final int MAX_POSITIVE_FRACTIONAL_LENGTH = 16;
    protected static final int MAX_NEGATIVE_FRACTIONAL_LENGTH = 17;

    private static final double[] POW_10 = {1E0, 1E1, 1E2, 1E3, 1E4, 1E5, 1E6, 1E7, 1E8, 1E9, 1E10, 1E11, 1E12, 1E13, 1E14};

    public static double parseDouble(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.value();
        int off = start;

        checkMinLength(off - end);

        byte b = buffer.getByte(off++);
        if (isDigit(b)) {
            long value = digit(b);

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
                            checkValueLength(off - start - 1, MAX_POSITIVE_FRACTIONAL_LENGTH);
                            offset.value(off);
                            return computeDouble(value, off - fractionalOffset - 1);
                        } else {
                            throwInvalidChar(b);
                        }
                    }

                } else if (b == separator) {
                    checkValueLength(off - start - 1, MAX_POSITIVE_INTEGER_LENGTH);
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
                    } else if (b == '.') {
                        int fractionalOffset = off;

                        while (off < end) {
                            b = buffer.getByte(off++);
                            if (isDigit(b)) {
                                value = (value << 3) + (value << 1) + digit(b);
                            } else if (b == separator) {
                                checkValueLength(off - start - 1, MAX_NEGATIVE_FRACTIONAL_LENGTH);
                                offset.value(off);
                                return -computeDouble(value, off - fractionalOffset - 1);
                            } else {
                                throwInvalidChar(b);
                            }
                        }

                    } else if (b == separator) {
                        checkValueLength(off - start - 1, MAX_NEGATIVE_INTEGER_LENGTH);
                        offset.value(off);
                        return -((double) value);
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

    protected static double computeDouble(long value, int fractionalLength) {
        return value / POW_10[fractionalLength]; // TODO: one can multiply by inverse, with bad accuracy?
    }

    protected static void checkValueLength(int length, int max) {
        if (length > max)
            throw new ParserException(String.format("number is too long, length %s, max %s", length, max));
    }

}
