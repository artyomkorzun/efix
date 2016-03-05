package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.format.LongFormatter.formatULong;
import static org.efix.util.format.LongFormatter.ulongLength;


public class DecimalFormatter {

    protected static final int MAX_NORMALIZED_SCALE = 14;

    public static int formatDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        if (value < 0) {
            buffer.putByte(offset++, (byte) '-');
            return formatUDecimal(-value, scale, buffer, offset);
        } else {
            return formatUDecimal(value, scale, buffer, offset);
        }
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
        int length = ulongLength(value);
        int max = DecimalType.MAX_DIGITS;

        if (length > max) {
            throw new IllegalArgumentException(
                    String.format("Normalized decimal %s with scale %s contains too many digits %s, max %s",
                            value, scale, length, max)
            );
        }

        if (scale > MAX_NORMALIZED_SCALE) {
            throw new IllegalArgumentException(
                    String.format("Normalized decimal %s with too big scale %s, max %s", value, scale, max)
            );
        }
    }

}
