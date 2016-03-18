package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.format.LongFormatter.formatULong;
import static org.efix.util.format.LongFormatter.ulongLength;


public class DecimalFormatter {

    protected static final int MAX_NORMALIZED_SCALE = 14;

    public static int formatDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        if (value >= 0)
            return formatUDecimal(value, scale, buffer, offset);
        else
            return formatNegativeDecimal(value, scale, buffer, offset);
    }

    public static int formatNegativeDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        checkNegativeDecimal(value);
        buffer.putByte(offset++, (byte) '-');
        return formatUDecimal(-value, scale, buffer, offset);
    }

    public static int formatUDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        if (value == 0)
            scale = 0;

        while (scale > 0 && (value % 10) == 0) {
            value /= 10;
            scale--;
        }

        checkUDecimal(value, scale);

        long multiplier = DecimalType.multiplier(scale);
        long integer = value / multiplier;
        long fractional = value - integer * multiplier;

        offset = formatULong(integer, buffer, offset);

        if (fractional > 0) {
            buffer.putByte(offset++, (byte) '.');
            int index = offset + scale;
            formatULong(fractional, buffer, offset, index);
            offset = index;
        }

        return offset;
    }

    private static void checkUDecimal(long value, int scale) {
        int digits = ulongLength(value);
        int max = DecimalType.MAX_DIGITS;

        if (digits > max) {
            throw new IllegalArgumentException(
                    String.format("Normalized decimal (unsigned with truncated zeros if any) %s with scale %s contains too many digits %s, max %s",
                            value, scale, digits, max)
            );
        }

        if (scale > MAX_NORMALIZED_SCALE) {
            throw new IllegalArgumentException(
                    String.format("Normalized decimal (unsigned with truncated zeros if any) %s with too big scale %s, max %s",
                            value, scale, MAX_NORMALIZED_SCALE)
            );
        }
    }

    protected static void checkNegativeDecimal(long value) {
        if (value == Long.MIN_VALUE)
            throw new IllegalArgumentException(String.format("Decimal %s contains too many digits", value));
    }

}
