package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.TimeType;

import static org.f1x.util.format.DateFormatter.DAY_MILLIS;
import static org.f1x.util.format.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.IntFormatter.format2DigitUInt;
import static org.f1x.util.format.IntFormatter.format3DigitUInt;

public class TimeFormatter {

    protected static final int SECOND_MILLIS = 1000;
    protected static final int MINUTE_MILLIS = SECOND_MILLIS * 60;
    protected static final int HOUR_MILLIS = MINUTE_MILLIS * 60;

    public static void formatTime(long timestamp, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, TimeType.MILLISECOND_TIME_LENGTH);
        formatTime(timestamp, buffer, off);
        offset.set(off + TimeType.MILLISECOND_TIME_LENGTH);
    }

    protected static void formatTime(long timestamp, MutableBuffer buffer, int offset) {
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

        format2DigitUInt(hour, buffer, offset + TimeType.HOUR_OFFSET);
        buffer.putByte(offset + TimeType.FIRST_COLON_OFFSET, (byte) ':');

        format2DigitUInt(minute, buffer, offset + TimeType.MINUTE_OFFSET);
        buffer.putByte(offset + TimeType.SECOND_COLON_OFFSET, (byte) ':');

        format2DigitUInt(second, buffer, offset + TimeType.SECOND_OFFSET);
        buffer.putByte(offset + TimeType.DOT_OFFSET, (byte) '.');

        format3DigitUInt(millis, buffer, offset + TimeType.MILLISECOND_OFFSET);
    }

}
