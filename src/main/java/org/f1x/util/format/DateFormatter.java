package org.f1x.util.format;

import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.IntFormatter.format2DigitUInt;
import static org.f1x.util.format.IntFormatter.format4DigitUInt;


public class DateFormatter {

    protected static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
    protected static final int DAYS_IN_YEAR = 365;
    protected static final int DAYS_IN_LEAP_YEAR = 366;
    protected static final int DAYS_IN_4_CYCLE = DAYS_IN_YEAR * 3 + DAYS_IN_LEAP_YEAR;

    protected static final int DAYS_TO_EPOCH = daysToYear(1970);
    protected static final int DAYS_TO_1904 = daysToYear(1904);
    protected static final int DAYS_TO_2100 = daysToYear(2100);

    protected static final long MILLIS_1904_TO_EPOCH = (DAYS_TO_EPOCH - DAYS_TO_1904) * DAY_MILLIS;
    protected static final long MILLIS_EPOCH_TO_2100 = (DAYS_TO_2100 - DAYS_TO_EPOCH) * DAY_MILLIS;

    private static final byte[] DAY_TO_MONTH = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,
            12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12
    };

    private static final byte[] DAY_TO_MONTH_LEAP = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,
            12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12
    };

    private static final int[] MONTH_TO_DAY_OFFSET = {0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final int[] MONTH_TO_DAY_OFFSET_LEAP = {0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    // TODO: add support for 0-1904 and 2100-9999 years + optimize
    public static int formatDate(long timestamp, MutableBuffer buffer, int offset) {
        checkTimestamp(timestamp);
        timestamp += MILLIS_1904_TO_EPOCH;

        int days = (int) (timestamp / DAY_MILLIS);
        int cycles4 = days / DAYS_IN_4_CYCLE;
        days -= cycles4 * DAYS_IN_4_CYCLE;

        int year = 1904 + (cycles4 << 2);
        int month;
        int day;

        if (days < DAYS_IN_LEAP_YEAR) {
            month = DAY_TO_MONTH_LEAP[days];
            day = days - MONTH_TO_DAY_OFFSET_LEAP[month] + 1;
        } else {
            year++;
            days -= DAYS_IN_LEAP_YEAR;

            int years = days / DAYS_IN_YEAR;
            year += years;
            days -= years * DAYS_IN_YEAR;

            month = DAY_TO_MONTH[days];
            day = days - MONTH_TO_DAY_OFFSET[month] + 1;
        }

        return formatDate(year, month, day, buffer, offset);
    }

    public static int formatDate(int year, int month, int day, MutableBuffer buffer, int offset) {
        offset = format4DigitUInt(year, buffer, offset);
        offset = format2DigitUInt(month, buffer, offset);
        offset = format2DigitUInt(day, buffer, offset);
        return offset;
    }

    protected static void checkTimestamp(long timestamp) {
        if (timestamp < -MILLIS_1904_TO_EPOCH || timestamp > MILLIS_EPOCH_TO_2100)
            throw new FormatterException(String.format("Timestamp %s is out of 1904-2100 years", timestamp));
    }

    private static int daysToYear(int year) {
        year--;
        return year * DAYS_IN_YEAR + year / 4 - year / 100 + year / 400;
    }

}
