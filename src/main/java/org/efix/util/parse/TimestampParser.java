package org.efix.util.parse;

import org.efix.message.InvalidFieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.TimestampType;


public class TimestampParser {

    private static final int DAYS_TO_EPOCH = 1969 * 365 + 1969 / 4 - 1969 / 100 + 1969 / 400;
    private static final int DAYS_TO_2000 = 1999 * 365 + 1999 / 4 - 1999 / 100 + 1999 / 400;
    private static final int DAYS_EPOCH_TO_2000 = DAYS_TO_2000 - DAYS_TO_EPOCH;

    private static final int DAYS_IN_YEAR = 365;

    private static final short[] DAYS_FROM_NEW_YEAR = {1, 32, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    private static final short[] DAYS_FROM_NEW_YEAR_LEAP = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    private static final int MINUTE_S = 60;
    private static final int HOUR_S = 60 * MINUTE_S;
    private static final long DAY_S = 24 * HOUR_S;

    private static final int SECOND_MS = 1000;
    private static final long SECOND_NS = 1000 * 1000 * 1000;

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS(.sssssssss). Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestampMs(final int tag, final Buffer buffer, final int offset, final int end) {
        final int length = end - offset;
        if (length < TimestampType.SECOND_TIMESTAMP_LENGTH) {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        final int days = parseDays(tag, buffer, offset);
        checkByte(tag, '-', buffer, offset + TimestampType.DASH_OFFSET);

        final int seconds = parseSeconds(tag, buffer, offset);
        long timestampMs = (DAY_S * days + seconds) * SECOND_MS;

        if (length >= TimestampType.MILLISECOND_TIMESTAMP_LENGTH) {
            checkByte(tag, '.', buffer, offset + TimestampType.DOT_OFFSET);
            timestampMs += parseMilliseconds(tag, buffer, offset);
        }

        return timestampMs;
    }

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS(.sssssssss). Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestampNs(final int tag, final Buffer buffer, final int offset, final int end) {
        final int length = end - offset;
        if (length < TimestampType.SECOND_TIMESTAMP_LENGTH) {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        final int days = parseDays(tag, buffer, offset);
        checkByte(tag, '-', buffer, offset + TimestampType.DASH_OFFSET);

        final int seconds = parseSeconds(tag, buffer, offset);
        long timestampNs = (DAY_S * days + seconds) * SECOND_NS;

        if (length >= TimestampType.MILLISECOND_TIMESTAMP_LENGTH) {
            checkByte(tag, '.', buffer, offset + TimestampType.DOT_OFFSET);
            timestampNs += parseNanoseconds(tag, buffer, offset, length);
        }

        return timestampNs;
    }

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(final byte separator, final Buffer buffer, final MutableInt offset, final int end) {
        final long date = DateParser.parseDate((byte) '-', buffer, offset, end);
        final int time = TimeParser.parseTime(separator, buffer, offset, end);
        return date + time;
    }

    private static int parseDays(final int tag, final Buffer buffer, final int offset) {
        final int year = parseYear(tag, buffer, offset + TimestampType.YEAR_OFFSET);
        final int month = parseMonth(tag, buffer, offset + TimestampType.MONTH_OFFSET);
        final int day = parseDay(tag, buffer, offset + TimestampType.DAY_OFFSET);

        final boolean leapYear = ((year & 0x3) == 0);
        final int daysFromNewYear = (leapYear ? DAYS_FROM_NEW_YEAR_LEAP[month] : DAYS_FROM_NEW_YEAR[month]);

        return DAYS_IN_YEAR * year + (year >> 2) + daysFromNewYear + day + DAYS_EPOCH_TO_2000;
    }

    private static int parseYear(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset);
        final byte b2 = buffer.getByte(offset + 1);
        final byte b3 = buffer.getByte(offset + 2);
        final byte b4 = buffer.getByte(offset + 3);

        if (b1 != '2' | b2 != '0' | b3 < '0' | b3 > '9' | b4 < '0' | b4 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b3 - '0') + (b4 - '0');
    }

    private static int parseMonth(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset);
        final byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '1' | b2 < '0' | b2 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // month - 1
    }

    private static int parseDay(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset);
        final byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '3' | b2 < '0' | b2 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // day - 1
    }

    private static int parseSeconds(final int tag, final Buffer buffer, final int offset) {
        final int hour = parseHour(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimestampType.FIRST_COLON_OFFSET);

        final int minute = parseMinute(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimestampType.SECOND_COLON_OFFSET);

        final int second = parseSecond(tag, buffer, offset);

        return HOUR_S * hour + MINUTE_S * minute + second;
    }

    private static int parseHour(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset + TimestampType.HOUR_OFFSET + 0);
        final byte b2 = buffer.getByte(offset + TimestampType.HOUR_OFFSET + 1);

        if (b1 < '0' | b1 > '2' | b2 < '0' | b2 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMinute(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset + TimestampType.MINUTE_OFFSET + 0);
        final byte b2 = buffer.getByte(offset + TimestampType.MINUTE_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseSecond(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset + TimestampType.SECOND_OFFSET + 0);
        final byte b2 = buffer.getByte(offset + TimestampType.SECOND_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMilliseconds(final int tag, final Buffer buffer, final int offset) {
        final byte b1 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 0);
        final byte b2 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 1);
        final byte b3 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 2);

        if (b1 < '0' | b1 > '9' | b2 < '0' | b2 > '9' | b3 < '0' | b3 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        return 100 * (b1 - '0') + 10 * (b2 - '0') + (b3 - '0');
    }

    private static int parseNanoseconds(final int tag, final Buffer buffer, final int offset, final int length) {
        final byte b1 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 0);
        final byte b2 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 1);
        final byte b3 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 2);

        if (b1 < '0' | b1 > '9' | b2 < '0' | b2 > '9' | b3 < '0' | b3 > '9') {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }

        int nanos = (b1 - '0') * 100000000 + (b2 - '0') * 10000000 + (b3 - '0') * 1000000;

        if (length >= TimestampType.MICROSECOND_TIMESTAMP_LENGTH) {
            final byte b4 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 3);
            final byte b5 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 4);
            final byte b6 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 5);

            if (b4 < '0' | b4 > '9' | b5 < '0' | b5 > '9' | b6 < '0' | b6 > '9') {
                throw new InvalidFieldException(tag, "Not valid timestamp");
            }

            nanos += (b4 - '0') * 100000 + (b5 - '0') * 10000 + (b6 - '0') * 1000;

            if (length >= TimestampType.NANOSECOND_TIMESTAMP_LENGTH) {
                final byte b7 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 6);
                final byte b8 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 7);
                final byte b9 = buffer.getByte(offset + TimestampType.SUB_SECOND_OFFSET + 8);

                if (b7 < '0' | b7 > '9' | b8 < '0' | b8 > '9' | b9 < '0' | b9 > '9') {
                    throw new InvalidFieldException(tag, "Not valid timestamp");
                }

                nanos += (b7 - '0') * 100 + (b8 - '0') * 10 + (b9 - '0');
            }
        }

        return nanos;
    }

    private static void checkByte(final int tag, final char expected, final Buffer buffer, final int offset) {
        byte b = buffer.getByte(offset);
        if (b != expected) {
            throw new InvalidFieldException(tag, "Not valid timestamp");
        }
    }

}
