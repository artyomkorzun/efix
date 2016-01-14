package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.DateFormatter.DAY_MILLIS;
import static org.f1x.util.format.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.IntFormatter.format2DigitUInt;
import static org.f1x.util.format.IntFormatter.format3DigitUInt;

public class TimeFormatter {

    protected static final int TIME_LENGTH = 12;

    protected static final int HOUR_OFFSET = 0;
    protected static final int FIST_COLON_OFFSET = 2;
    protected static final int MINUTE_OFFSET = 3;
    protected static final int SECOND_COLON_OFFSET = 5;
    protected static final int SECOND_OFFSET = 6;
    protected static final int DOT_OFFSET = 8;
    protected static final int MILLISECOND_OFFSET = 9;

    protected static final int SECOND_MILLIS = 1000;
    protected static final int MINUTE_MILLIS = SECOND_MILLIS * 60;
    protected static final int HOUR_MILLIS = MINUTE_MILLIS * 60;

    public static void formatTime(long timestamp, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, TIME_LENGTH);
        formatTime(timestamp, buffer, off);
        offset.value(off + TIME_LENGTH);
    }

    protected static void formatTime(long timestamp, MutableBuffer buffer, int offset) {
        int days = (int) (timestamp / DAY_MILLIS);
        if (timestamp < 0)
            days--;

        int millis = (int) (timestamp - days * DAY_MILLIS);

        int hour = millis / HOUR_MILLIS;
        millis -= hour * HOUR_MILLIS;

        int minute = millis / MINUTE_MILLIS;
        millis -= minute * MINUTE_MILLIS;

        int second = millis / SECOND_MILLIS;
        millis -= second * SECOND_MILLIS;

        format2DigitUInt(hour, buffer, offset + HOUR_OFFSET);
        buffer.putByte(offset + FIST_COLON_OFFSET, (byte) ':');

        format2DigitUInt(minute, buffer, offset + MINUTE_OFFSET);
        buffer.putByte(offset + SECOND_COLON_OFFSET, (byte) ':');

        format2DigitUInt(second, buffer, offset + SECOND_OFFSET);
        buffer.putByte(offset + DOT_OFFSET, (byte) '.');

        format3DigitUInt(millis, buffer, offset + MILLISECOND_OFFSET);
    }

}
