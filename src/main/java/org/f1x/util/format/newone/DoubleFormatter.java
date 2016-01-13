package org.f1x.util.format.newone;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.newone.FormatterUtil.checkFreeSpace;

public class DoubleFormatter {

    protected static final int MAX_PRECISION = 15;
    protected static final int MAX_LENGTH = 17;
    protected static final double MAX_VALUE = 1E15 - 1;
    protected static final double MIN_VALUE = -MAX_VALUE;


    private static final long[] MULTIPLIER = {
            0,
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
            1000000000000000L
    };

    public static void formatDouble(double value, int precision, MutableBuffer buffer, MutableInt offset, int end) {
        formatDouble(value, precision, true, buffer, offset, end);
    }

    // TODO optimize
    public static void formatDouble(double value, int precision, boolean roundHalfUp, MutableBuffer buffer, MutableInt offset, int end) {
        checkValue(value);

        if (precision <= 0) {
            long integer = round(value, roundHalfUp);
            LongFormatter.formatLong(integer, buffer, offset, end);
            return;
        }

        checkFreeSpace(end - offset.value(), MAX_LENGTH);

        long integer = (long) value;
        if (integer < 0) {
            value = -value;
            integer = -integer;
            CharFormatter.formatChar('-', buffer, offset, end);
        }

        int length = LongFormatter.ulongLength(integer);
        if (precision > MAX_PRECISION - length)
            throw new FormatterException(String.format("value %s contains more than 15 significant digits, precision %s", value, precision));

        LongFormatter.formatULong(integer, buffer, offset, end);
        long fractional = round((value - integer) * MULTIPLIER[precision], roundHalfUp);

        if (fractional > 0) {
            CharFormatter.formatChar('.', buffer, offset, end);

            int leadingZeros = precision - LongFormatter.ulongLength(fractional);
            for (int i = 0; i < leadingZeros; i++)
                CharFormatter.formatChar('0', buffer, offset, end);

            LongFormatter.formatULong(truncateZeros(fractional), buffer, offset, end);
        }
    }

    protected static long round(double value, boolean roundHalfUp) {
        if (roundHalfUp) {
            return value >= 0 ? Math.round(value) : -Math.round(-value);
        } else {
            return value >= 0 ? -Math.round(-value) : Math.round(value);
        }
    }

    protected static long truncateZeros(long value) {
        while (value % 10 == 0)
            value /= 10;

        return value;
    }

    /**
     * Checks value bounds. Note: checks NaN and infinities as well
     */
    protected static void checkValue(double value) {
        if (!(MIN_VALUE <= value && value <= MAX_VALUE))
            throw new FormatterException("invalid value " + value);
    }

}
