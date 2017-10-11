package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;

import java.util.concurrent.TimeUnit;

import static org.efix.util.format.DateFormatter.DAY_MS;
import static org.efix.util.format.DateFormatter.DAY_NS;
import static org.efix.util.format.FormatterUtil.digit;
import static org.efix.util.format.IntFormatter.format2DigitUInt;
import static org.efix.util.format.IntFormatter.format3DigitUInt;

public class TimeFormatter {

    protected static final int SECOND_MS = 1000;
    protected static final int MINUTE_MS = SECOND_MS * 60;
    protected static final int HOUR_MS = MINUTE_MS * 60;

    protected static final long SECOND_NS = SECOND_MS * 1000L * 1000;
    protected static final long MINUTE_NS = MINUTE_MS * 1000L * 1000;
    protected static final long HOUR_NS = HOUR_MS * 1000L * 1000;

    // protected static final int MULTIPLIER[] = {1, 1000, 1000 * 1000, 1000 * 1000 * 1000};
    protected static final int LENGTH[] = {9, 6, 3, 0};

    protected static int formatTime(long timestamp, TimeUnit unit, MutableBuffer buffer, int offset) {
        final int unitIndex = unit.ordinal();
        long timestampNs = unit.toNanos(timestamp);
        int days = (int) (timestampNs / DAY_NS);

        long nanos = (timestampNs - days * DAY_NS);

        final int hour = (int) (nanos / HOUR_NS);
        nanos -= hour * HOUR_NS;

        final int minute = (int) (nanos / MINUTE_NS);
        nanos -= minute * MINUTE_NS;

        final int second = (int) (nanos / SECOND_NS);
        nanos -= second * SECOND_NS;

        return formatTime(hour, minute, second, timestamp, unitIndex, buffer, offset);
    }

    public static int formatTime(long timestamp, MutableBuffer buffer, int offset) {
        int days = (int) (timestamp / DAY_MS);

        int millis = (int) (timestamp - days * DAY_MS);
        if (millis < 0)
            millis += DAY_MS;

        int hour = millis / HOUR_MS;
        millis -= hour * HOUR_MS;

        int minute = millis / MINUTE_MS;
        millis -= minute * MINUTE_MS;

        int second = millis / SECOND_MS;
        millis -= second * SECOND_MS;

        return formatTime(hour, minute, second, millis, buffer, offset);
    }

    public static int formatTime(int hour, int minute, int second, int millis, MutableBuffer buffer, int offset) {
        offset = format2DigitUInt(hour, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(minute, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(second, buffer, offset);
        buffer.putByte(offset++, (byte) '.');

        offset = format3DigitUInt(millis, buffer, offset);
        return offset;
    }

    protected static int formatTime(int hour, int minute, int second, long timestamp, int unitIndex, MutableBuffer buffer, int offset) {
        offset = format2DigitUInt(hour, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(minute, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(second, buffer, offset);

        final int length = LENGTH[unitIndex];

        if (length > 0) {
            buffer.putByte(offset++, (byte) '.');
            offset = IntFormatter.formatNDigitUInt((int) (timestamp % 1000000000), length, buffer, offset);
        }

        return offset;
    }

    protected static int formatUInt(long value, int length, MutableBuffer buffer, int offset) {
        int index = offset + length;

        while (index > offset) {
            int integer = (int) ((value * 3435973837L) >>> (32 + 3));
            int remainder = 0;// ()value - ((integer << 3) + (integer << 1));
            buffer.putByte(--index, digit(remainder));
            value = integer;
        }

        return offset + length;
    }

}
