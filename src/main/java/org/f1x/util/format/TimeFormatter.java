package org.f1x.util.format;

import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.DateFormatter.DAY_MILLIS;
import static org.f1x.util.format.IntFormatter.format2DigitUInt;
import static org.f1x.util.format.IntFormatter.format3DigitUInt;

public class TimeFormatter {

    protected static final int SECOND_MILLIS = 1000;
    protected static final int MINUTE_MILLIS = SECOND_MILLIS * 60;
    protected static final int HOUR_MILLIS = MINUTE_MILLIS * 60;

    public static int formatTime(long timestamp, MutableBuffer buffer, int offset) {
        int days = (int) (timestamp / DAY_MILLIS);

        int millis = (int) (timestamp - days * DAY_MILLIS);
        if (millis < 0)
            millis += DAY_MILLIS;

        int hour = millis / HOUR_MILLIS;
        millis -= hour * HOUR_MILLIS;

        int minute = millis / MINUTE_MILLIS;
        millis -= minute * MINUTE_MILLIS;

        int second = millis / SECOND_MILLIS;
        millis -= second * SECOND_MILLIS;

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

}
