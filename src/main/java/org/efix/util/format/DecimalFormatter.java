package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.DecimalType;

import static org.efix.util.format.LongFormatter.formatULong;


public class DecimalFormatter {

    private static final Buffer MIN_DECIMAL[] = {
            BufferUtil.fromString("-9223372036854775808"),
            BufferUtil.fromString("-922337203685477580.8"),
            BufferUtil.fromString("-92233720368547758.08"),
            BufferUtil.fromString("-9223372036854775.808"),
            BufferUtil.fromString("-922337203685477.5808"),
            BufferUtil.fromString("-92233720368547.75808"),
            BufferUtil.fromString("-9223372036854.775808"),
            BufferUtil.fromString("-922337203685.4775808"),
            BufferUtil.fromString("-92233720368.54775808"),
            BufferUtil.fromString("-9223372036.854775808"),
            BufferUtil.fromString("-922337203.6854775808"),
            BufferUtil.fromString("-92233720.36854775808"),
            BufferUtil.fromString("-9223372.036854775808"),
            BufferUtil.fromString("-922337.2036854775808"),
            BufferUtil.fromString("-92233.72036854775808"),
            BufferUtil.fromString("-9223.372036854775808"),
            BufferUtil.fromString("-922.3372036854775808"),
            BufferUtil.fromString("-92.23372036854775808"),
            BufferUtil.fromString("-9.223372036854775808"),
    };

    public static int formatDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        if (value < 0) {
            if (value == Long.MIN_VALUE) {
                return formatMinDecimal(scale, buffer, offset);
            }

            buffer.putByte(offset++, (byte) '-');
            value = -value;
        }

        return formatUDecimal(value, scale, buffer, offset);
    }

    public static int formatUDecimal(long value, int scale, MutableBuffer buffer, int offset) {
        final long multiplier = DecimalType.multiplier(scale);
        final long integer = value / multiplier;
        long fractional = value - integer * multiplier;

        offset = formatULong(integer, buffer, offset);

        if (fractional > 0) {
            while (true) {
                final long newFractional = fractional / 10;
                final int remainder = (int) (fractional - newFractional * 10);
                if (remainder != 0) {
                    break;
                }

                fractional = newFractional;
                scale--;
            }

            buffer.putByte(offset++, (byte) '.');
            int index = offset + scale;
            formatULong(fractional, buffer, offset, index);
            offset = index;
        }

        return offset;
    }

    private static int formatMinDecimal(final int scale, final MutableBuffer buffer, final int offset) {
        final Buffer minValue = MIN_DECIMAL[scale];
        final int length = minValue.capacity();
        buffer.putBytes(offset, minValue, 0, length);
        return offset + length;
    }

}
