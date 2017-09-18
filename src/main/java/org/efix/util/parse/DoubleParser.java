package org.efix.util.parse;

import org.efix.message.InvalidFieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DoubleType;

import static org.efix.util.parse.ParserUtil.*;


public class DoubleParser {

    private static final double[] INVERSE_POW_10 = {1E0, 1E-1, 1E-2, 1E-3, 1E-4, 1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11, 1E-12, 1E-13, 1E-14, 1E-15, 1E-16, 1E-17, 1E-18};

    public static double parseDouble(int tag, Buffer buffer, int offset, int end) {
        long value = 0;
        boolean sign = true;

        if (buffer.getByte(offset) == '-') {
            offset++;
            sign = false;
        }

        while (offset < end) {
            byte b = buffer.getByte(offset++);
            if (b >= '0' & b <= '9') {
                value = 10 * value - (b - '0');
            } else if (b == '.') {
                break;
            } else {
                throw new InvalidFieldException(tag, "Not valid decimal");
            }
        }

        if (offset == end) {
            return sign ? -value : value;
        }

        do {
            byte b = buffer.getByte(offset++);
            if (b < '0' | b > '9') {
                throw new InvalidFieldException(tag, "Not valid decimal");
            }

            value = 10 * value - (b - '0');
        } while (offset < end);

        int scale = end - offset;
        double result = value * INVERSE_POW_10[scale];

        return sign ? -result : result;
    }

    public static double parseUDouble(int tag, Buffer buffer, int offset, int end) {
        long value = 0;

        do {
            byte b = buffer.getByte(offset++);
            if (b >= '0' & b <= '9') {
                value = 10 * value + (b - '0');
            } else if (b == '.') {
                break;
            } else {
                throw new InvalidFieldException(tag, "Not valid double");
            }
        } while (offset < end);

        if (offset == end) {
            return value;
        }

        do {
            byte b = buffer.getByte(offset++);
            if (b < '0' | b > '9') {
                throw new InvalidFieldException(tag, "Not valid double");
            }

            value = 10 * value + (b - '0');
        } while (offset < end);

        int scale = end - offset;
        return value * INVERSE_POW_10[scale];
    }

    public static double parseDouble(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(SIGN_LENGTH, end - off);

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

        checkBounds(DoubleType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwUnexpectedByte(b);

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
                        int digits = off - start - SEPARATOR_LENGTH - DOT_LENGTH;
                        checkDouble(digits);
                        offset.set(off);
                        return toDouble(value, off - fractionalOffset - SEPARATOR_LENGTH);
                    } else {
                        throwUnexpectedByte(b);
                    }
                }

            } else if (b == separator) {
                int digits = off - start - SEPARATOR_LENGTH;
                checkDouble(digits);
                offset.set(off);
                return value;
            } else {
                throwUnexpectedByte(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static double toDouble(long value, int scale) {
        return value * INVERSE_POW_10[scale];
    }

    protected static void checkDouble(int digits) {
        if (digits > DoubleType.MAX_DIGITS)
            throw new ParserException(String.format("Decimal contains too many digits %s, max %s", digits, DoubleType.MAX_DIGITS));
    }

}
