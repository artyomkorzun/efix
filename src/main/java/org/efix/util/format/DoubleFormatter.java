package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.DoubleType;

import static org.efix.util.format.LongFormatter.formatULong;
import static org.efix.util.format.LongFormatter.ulongLength;


public class DoubleFormatter {

    protected static final int MAX_PRECISION = 15;

    private static final long[] MULTIPLIER = {
            1L,
            10L,
            100L,
            1000L,
            10000L,
            100000L,
            1000000L,
            10000000L,
            100000000L,
            1000000000L,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
    };

    public static int formatDouble(double value, int precision, MutableBuffer buffer, int offset) {
        return formatDouble(value, precision, true, buffer, offset);
    }

    public static int formatDouble(double value, int precision, boolean roundHalfUp, MutableBuffer buffer, int offset) {
        if (value < 0) {
            checkNegativeDouble(value);
            value = -value;
            buffer.putByte(offset++, (byte) '-');
        } else {
            checkUDouble(value);
        }

        precision = correctPrecision(value, precision);
        long multiplier = MULTIPLIER[precision];
        long number = roundUDouble(value * multiplier, roundHalfUp);

        long integer = number / multiplier;
        long fractional = number - integer * multiplier;

        while (precision > 0 && (fractional % 10 == 0)) {
            fractional /= 10;
            precision--;
        }

        offset = formatULong(integer, buffer, offset);

        if (precision > 0) {
            buffer.putByte(offset++, (byte) '.');
            int index = offset + precision;
            formatULong(fractional, buffer, offset, index);
            offset = index;
        }

        return offset;
    }

    protected static long roundUDouble(double value, boolean roundHalfUp) {
        return roundHalfUp ? Math.round(value) : -Math.round(-value);
    }

    protected static int correctPrecision(double value, int precision) {
        if (precision <= 0)
            return 0;

        long integer = (long) value;
        int length = ulongLength(integer);
        return Math.min(precision, MAX_PRECISION - length);
    }

    /**
     * Checks NaN and positive infinity as well
     */
    protected static void checkUDouble(double value) {
        if (!(value <= DoubleType.MAX_VALUE))
            throw new FormatterException(String.format("Value %s is out of range [%s, %s]", value, DoubleType.MIN_VALUE, DoubleType.MAX_VALUE));
    }

    /**
     * Checks NaN and negative infinity as well
     */
    protected static void checkNegativeDouble(double value) {
        if (!(value >= DoubleType.MIN_VALUE))
            throw new FormatterException(String.format("Value %s is out of range [%s, %s]", value, DoubleType.MIN_VALUE, DoubleType.MAX_VALUE));
    }

}
