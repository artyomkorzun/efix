package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.DoubleType;

import static org.f1x.util.format.CharFormatter.formatChar;
import static org.f1x.util.format.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.LongFormatter.*;

public class DoubleFormatter {

    protected static final int MAX_PRECISION = 15;
    protected static final int MAX_LENGTH = 17;

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
            formatLong(integer, buffer, offset, end);
            return;
        }

        checkFreeSpace(end - offset.get(), DoubleType.MAX_LENGTH);

        long integer = (long) value;
        if (integer < 0) {
            value = -value;
            integer = -integer;
            formatChar('-', buffer, offset, end);
        }

        int length = ulongLength(integer);
        if (precision > MAX_PRECISION - length)
            throw new FormatterException(String.format("value %s contains more than 15 significant digits, precision %s", value, precision));

        formatULong(integer, buffer, offset, end);
        long fractional = round((value - integer) * MULTIPLIER[precision], roundHalfUp);

        if (fractional > 0) {
            formatChar('.', buffer, offset, end);

            int leadingZeros = precision - ulongLength(fractional);
            for (int i = 0; i < leadingZeros; i++)
                formatChar('0', buffer, offset, end);

            formatULong(truncateZeros(fractional), buffer, offset, end);
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
        if (!(DoubleType.MIN_VALUE <= value && value <= DoubleType.MAX_VALUE))
            throw new FormatterException("invalid value " + value);
    }

}
