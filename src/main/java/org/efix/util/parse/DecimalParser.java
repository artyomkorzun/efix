package org.efix.util.parse;

import org.efix.message.FieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.parse.ParserUtil.*;


public class DecimalParser {

    public static long parseDecimalHalfUp(int tag, int scale, Buffer buffer, int offset, int end) {
        long value = 0;
        boolean sign = true;

        if (buffer.getByte(offset) == '-') {
            offset++;
            sign = false;
        }

        if (offset < end) {
            do {
                byte b = buffer.getByte(offset++);
                if (b >= '0' & b <= '9') {
                    value = 10 * value - (b - '0');
                } else if (b == '.') {
                    break;
                } else {
                    throw new FieldException(tag, "Not valid decimal");
                }
            } while (offset < end);

            if (offset < end) {
                int limit = Math.min(offset + scale, end);
                scale -= end - offset;

                while (offset < limit) {
                    byte b = buffer.getByte(offset++);
                    if (b < '0' | b > '9') {
                        throw new FieldException(tag, "Not valid decimal");
                    }

                    value = 10 * value - (b - '0');
                }

                if (scale < 0) {
                    byte b = buffer.getByte(offset);
                    if (b < '0' | b > '9') {
                        throw new FieldException(tag, "Not valid decimal");
                    }

                    value -= b < '5' ? 0 : 1;
                    scale = 0;
                }
            }

            value *= DecimalType.multiplier(scale);
            value = sign ? -value : value;
        }

        return value;
    }

    public static long parseUDecimalHalfUp(int tag, int scale, Buffer buffer, int offset, int end) {
        long value = 0;

        do {
            byte b = buffer.getByte(offset++);
            if (b >= '0' & b <= '9') {
                value = 10 * value + (b - '0');
            } else if (b == '.') {
                break;
            } else {
                throw new FieldException(tag, "Not valid decimal");
            }
        } while (offset < end);

        if (offset < end) {
            int limit = Math.min(offset + scale, end);
            scale -= end - offset;

            while (offset < limit) {
                byte b = buffer.getByte(offset++);
                if (b < '0' | b > '9') {
                    throw new FieldException(tag, "Not valid decimal");
                }

                value = 10 * value + (b - '0');
            }

            if (scale < 0) {
                byte b = buffer.getByte(offset);
                if (b < '0' | b > '9') {
                    throw new FieldException(tag, "Not valid decimal");
                }

                value += b < '5' ? 0 : 1;
                scale = 0;
            }
        }

        value *= DecimalType.multiplier(scale);
        return value;
    }

    public static long parseDecimal(int tag, int scale, Buffer buffer, int offset, int end) {
        long value = 0;
        boolean sign = true;

        if (buffer.getByte(offset) == '-') {
            offset++;
            sign = false;
        }

        if (offset < end) {
            do {
                byte b = buffer.getByte(offset++);
                if (b >= '0' & b <= '9') {
                    value = 10 * value - (b - '0');
                } else if (b == '.') {
                    break;
                } else {
                    throw new FieldException(tag, "Not valid decimal");
                }
            } while (offset < end);

            if (offset < end) {
                scale -= end - offset;
                if (scale < 0) {
                    throw new FieldException(tag, "Not valid decimal, too long fractional part");
                }

                do {
                    byte b = buffer.getByte(offset++);
                    if (b < '0' | b > '9') {
                        throw new FieldException(tag, "Not valid decimal");
                    }

                    value = 10 * value - (b - '0');
                } while (offset < end);
            }

            value *= DecimalType.multiplier(scale);
            value = sign ? -value : value;
        }

        return value;
    }

    public static long parseUDecimal(int tag, int scale, Buffer buffer, int offset, int end) {
        long value = 0;

        do {
            byte b = buffer.getByte(offset++);
            if (b >= '0' & b <= '9') {
                value = 10 * value + (b - '0');
            } else if (b == '.') {
                break;
            } else {
                throw new FieldException(tag, "Not valid decimal");
            }
        } while (offset < end);

        if (offset < end) {
            scale -= end - offset;
            if (scale < 0) {
                throw new FieldException(tag, "Not valid decimal, too long fractional part");
            }

            do {
                byte b = buffer.getByte(offset++);
                if (b < '0' | b > '9') {
                    throw new FieldException(tag, "Not valid decimal");
                }

                value = 10 * value + (b - '0');
            } while (offset < end);
        }

        value *= DecimalType.multiplier(scale);
        return value;
    }

    public static long parseDecimal(int scale, byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(SIGN_LENGTH, end - off);

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

        checkBounds(DecimalType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwUnexpectedByte(b);

        do {
            b = buffer.getByte(off++);

            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == '.') {
                int integerDigits = off - start - DOT_LENGTH;
                checkInteger(integerDigits, scale);
                start = off;

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) + digit(b);
                    } else if (b == separator) {
                        int fractionalDigits = off - start - SEPARATOR_LENGTH;
                        checkFractional(integerDigits, fractionalDigits, scale);
                        offset.set(off);
                        return value * DecimalType.multiplier(scale - fractionalDigits);
                    } else {
                        throwUnexpectedByte(b);
                    }
                }

            } else if (b == separator) {
                int digits = off - start - SEPARATOR_LENGTH;
                checkInteger(digits, scale);
                offset.set(off);
                return value * DecimalType.multiplier(scale);
            } else {
                throwUnexpectedByte(b);
            }

        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    public static long parseDecimal(int scale, boolean roundUp, byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(SIGN_LENGTH, end - off);

        if (buffer.getByte(off) == '-') {
            offset.set(off + SIGN_LENGTH);
            return -parseUDecimal(scale, roundUp, separator, buffer, offset, end);
        } else {
            return parseUDecimal(scale, roundUp, separator, buffer, offset, end);
        }
    }

    public static long parseUDecimal(int scale, boolean roundUp, byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkBounds(DecimalType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwUnexpectedByte(b);

        do {
            b = buffer.getByte(off++);

            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == '.') {
                int integerDigits = off - start - DOT_LENGTH;
                checkInteger(integerDigits, scale);
                start = off;

                while (off < end) {
                    b = buffer.getByte(off++);
                    if (isDigit(b)) {
                        value = (value << 3) + (value << 1) + digit(b);
                    } else if (b == separator) {
                        int fractionalDigits = off - start - SEPARATOR_LENGTH;
                        checkDecimal(integerDigits, fractionalDigits);
                        offset.set(off);
                        return round(value, fractionalDigits, scale, roundUp);
                    } else {
                        throwUnexpectedByte(b);
                    }
                }

            } else if (b == separator) {
                int digits = off - start - SEPARATOR_LENGTH;
                checkInteger(digits, scale);
                offset.set(off);
                return value * DecimalType.multiplier(scale);
            } else {
                throwUnexpectedByte(b);
            }

        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static long round(long value, int fractionalDigits, int scale, boolean roundUp) {
        if (fractionalDigits <= scale) {
            return value * DecimalType.multiplier(scale - fractionalDigits);
        } else {
            long multiplier = DecimalType.multiplier(fractionalDigits - scale);
            long truncatedValue = value / multiplier;
            long remainder = value - truncatedValue * multiplier;

            remainder = (remainder << 3) + (remainder << 1);
            multiplier += (multiplier << 2);

            if (roundUp) {
                if (remainder >= multiplier)
                    truncatedValue++;
            } else {
                if (remainder > multiplier)
                    truncatedValue++;
            }

            return truncatedValue;
        }
    }

    protected static void checkInteger(int digits, int scale) {
        int max = Math.min(DecimalType.MAX_DIGITS, DecimalType.MAX_SCALE - scale);
        if (digits > max)
            throw new ParserException(String.format("Decimal contains too many integer digits %s, scale %s", digits, scale));
    }

    protected static void checkFractional(int integerDigits, int fractionalDigits, int scale) {
        int max = Math.min(DecimalType.MAX_DIGITS - integerDigits, scale);
        if (fractionalDigits > max) {
            throw new ParserException(
                    String.format("Decimal contains too many digits, integer %s, fractional %s, scale %s",
                            integerDigits, fractionalDigits, scale)
            );
        }
    }

    protected static void checkDecimal(int integerDigits, int fractionalDigits) {
        if (integerDigits + fractionalDigits > DecimalType.MAX_DIGITS) {
            throw new ParserException(
                    String.format("Decimal contains too many digits, integer %s, fractional %s", integerDigits, fractionalDigits)
            );
        }
    }

}
