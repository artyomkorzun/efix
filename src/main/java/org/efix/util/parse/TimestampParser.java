package org.efix.util.parse;

import org.efix.message.FieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.TimestampType;


public class TimestampParser {

    private static final int DAYS_TO_EPOCH = 1969 * 365 + 1969 / 4 - 1969 / 100 + 1969 / 400;
    private static final int DAYS_TO_2000 = 1999 * 365 + 1999 / 4 - 1999 / 100 + 1999 / 400;
    private static final int DAYS_EPOCH_TO_2000 = DAYS_TO_2000 - DAYS_TO_EPOCH;
    private static final long DAY_MS = 24 * 60 * 60 * 1000;


    private static final short[] DAYS_FROM_NEW_YEAR = {1, 32, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    private static final short[] DAYS_FROM_NEW_YEAR_LEAP = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};


    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss(ssssss). Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(int tag, Buffer buffer, int offset, int end) {
        int length = end - offset;
        if (length < TimestampType.SECOND_TIMESTAMP_LENGTH) {
            throw new FieldException(tag, "Not valid timestamp");
        }

        int days = parseDays(tag, buffer, offset);
        checkByte(tag, '-', buffer, offset + TimestampType.DASH_OFFSET);

        int seconds = parseSeconds(tag, buffer, offset);
        int milliseconds = 0;

        if (length == TimestampType.MILLISECOND_TIMESTAMP_LENGTH) {
            checkByte(tag, '.', buffer, offset + TimestampType.DOT_OFFSET);
            milliseconds = parseMilliseconds(tag, buffer, offset);
        } else if (length != TimestampType.SECOND_TIMESTAMP_LENGTH) {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return days * DAY_MS + 1000 * seconds + milliseconds;
    }

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        long date = DateParser.parseDate((byte) '-', buffer, offset, end);
        int time = TimeParser.parseTime(separator, buffer, offset, end);
        return date + time;
    }

    private static int parseDays(int tag, Buffer buffer, int offset) {
        int year = parseYear(tag, buffer, offset + TimestampType.YEAR_OFFSET);
        int month = parseMonth(tag, buffer, offset + TimestampType.MONTH_OFFSET);
        int day = parseDay(tag, buffer, offset + TimestampType.DAY_OFFSET);

        boolean leapYear = ((year & 0x3) == 0);
        int daysFromNewYear = (leapYear ? DAYS_FROM_NEW_YEAR_LEAP[month] : DAYS_FROM_NEW_YEAR[month]);

        return 365 * year + (year >> 2) + daysFromNewYear + day + DAYS_EPOCH_TO_2000;
    }

    private static int parseYear(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset);
        byte b2 = buffer.getByte(offset + 1);
        byte b3 = buffer.getByte(offset + 2);
        byte b4 = buffer.getByte(offset + 3);

        if (b1 != '2' | b2 != '0' | b3 < '0' | b3 > '9' | b4 < '0' | b4 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b3 - '0') + (b4 - '0');
    }

    private static int parseMonth(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset);
        byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '1' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // month - 1
    }

    private static int parseDay(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset);
        byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '3' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // day - 1
    }

    private static int parseSeconds(int tag, Buffer buffer, int offset) {
        int hour = parseHour(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimestampType.FIRST_COLON_OFFSET);

        int minute = parseMinute(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimestampType.SECOND_COLON_OFFSET);

        int second = parseSecond(tag, buffer, offset);

        return 3600 * hour + 60 * minute + second;
    }

    private static int parseHour(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimestampType.HOUR_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimestampType.HOUR_OFFSET + 1);

        if (b1 < '0' | b1 > '2' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMinute(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimestampType.MINUTE_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimestampType.MINUTE_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseSecond(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimestampType.SECOND_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimestampType.SECOND_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMilliseconds(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimestampType.MILLISECOND_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimestampType.MILLISECOND_OFFSET + 1);
        byte b3 = buffer.getByte(offset + TimestampType.MILLISECOND_OFFSET + 2);

        if (b1 < '0' | b1 > '9' | b2 < '0' | b2 > '9' | b3 < '0' | b3 > '9') {
            throw new FieldException(tag, "Not valid timestamp");
        }

        return 100 * (b1 - '0') + 10 * (b2 - '0') + (b3 - '0');
    }

    private static void checkByte(int tag, char expected, Buffer buffer, int offset) {
        byte b = buffer.getByte(offset);
        if (b != expected) {
            throw new FieldException(tag, "Not valid timestamp");
        }
    }

}
