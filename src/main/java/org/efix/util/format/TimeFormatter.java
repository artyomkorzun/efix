package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;

import static org.efix.util.format.DateFormatter.DAY_MS;
import static org.efix.util.format.DateFormatter.DAY_NS;
import static org.efix.util.format.IntFormatter.*;


public class TimeFormatter {

    protected static final int SECOND_MS = 1000;
    protected static final int MINUTE_MS = SECOND_MS * 60;
    protected static final int HOUR_MS = MINUTE_MS * 60;

    protected static final long SECOND_NS = SECOND_MS * 1000L * 1000;
    protected static final long MINUTE_NS = MINUTE_MS * 1000L * 1000;
    protected static final long HOUR_NS = HOUR_MS * 1000L * 1000;

    protected static int formatTimeNs(final long timestampNs, final MutableBuffer buffer, final int offset) {
        final int days = (int) (timestampNs / DAY_NS);
        long nanos = (timestampNs - days * DAY_NS);

        final int hour = (int) (nanos / HOUR_NS);
        nanos -= hour * HOUR_NS;

        final int minute = (int) (nanos / MINUTE_NS);
        nanos -= minute * MINUTE_NS;

        final int second = (int) (nanos / SECOND_NS);
        nanos -= second * SECOND_NS;

        return formatTimeNs(hour, minute, second, (int) nanos, buffer, offset);
    }

    public static int formatTimeMs(long timestamp, MutableBuffer buffer, int offset) {
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

        return formatTimeMs(hour, minute, second, millis, buffer, offset);
    }

    public static int formatTimeMs(int hour, int minute, int second, int millis, MutableBuffer buffer, int offset) {
        offset = format2DigitUInt(hour, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(minute, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(second, buffer, offset);
        buffer.putByte(offset++, (byte) '.');

        offset = format3DigitUInt(millis, buffer, offset);
        return offset;
    }

    protected static int formatTimeNs(final int hour, final int minute, final int second, final int nanos, final MutableBuffer buffer, int offset) {
        offset = format2DigitUInt(hour, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(minute, buffer, offset);
        buffer.putByte(offset++, (byte) ':');

        offset = format2DigitUInt(second, buffer, offset);
        buffer.putByte(offset++, (byte) '.');

        offset = format9DigitUInt(nanos, buffer, offset);
        return offset;
    }

}
