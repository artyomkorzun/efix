package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;

import static org.efix.util.format.IntFormatter.format2DigitUInt;
import static org.efix.util.format.IntFormatter.format4DigitUInt;


public class DateFormatter {

    protected static final long DAY_MS = 24 * 60 * 60 * 1000;
    protected static final long DAY_NS = DAY_MS * 1000 * 1000;

    protected static final int DAYS_IN_YEAR = 365;
    protected static final int DAYS_IN_LEAP_YEAR = 366;
    protected static final int DAYS_IN_4_CYCLE = DAYS_IN_YEAR * 3 + DAYS_IN_LEAP_YEAR;

    protected static final int DAYS_TO_EPOCH = daysToYear(1970);
    protected static final int DAYS_TO_2000 = daysToYear(2000);
    protected static final int DAYS_TO_2100 = daysToYear(2100);

    protected static final long EPOCH_TO_2000_MS = (DAYS_TO_2000 - DAYS_TO_EPOCH) * DAY_MS;
    protected static final long EPOCH_TO_2100_MS = (DAYS_TO_2100 - DAYS_TO_EPOCH) * DAY_MS;

    protected static final long EPOCH_TO_2000_NS = (DAYS_TO_2000 - DAYS_TO_EPOCH) * DAY_NS;
    protected static final long EPOCH_TO_2100_NS = (DAYS_TO_2100 - DAYS_TO_EPOCH) * DAY_NS;


    private static final byte[] MONTH = {
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

    private static final byte[] MONTH_LEAP = {
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

    private static final short[] DAYS = {0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final short[] DAYS_LEAP = {0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    protected static int formatDateNs(final long timestampNs, final MutableBuffer buffer, final int offset) {
        checkTimestampNs(timestampNs);

        int days = (int) ((timestampNs - EPOCH_TO_2000_NS) / DAY_NS);
        final int cycles4 = days / DAYS_IN_4_CYCLE;
        days -= cycles4 * DAYS_IN_4_CYCLE;

        int year = 2000 + (cycles4 << 2);
        int month;
        int day;

        if (days < DAYS_IN_LEAP_YEAR) {
            month = MONTH_LEAP[days];
            day = days - DAYS_LEAP[month] + 1;
        } else {
            days--;
            int years = days / DAYS_IN_YEAR;
            year += years;
            days -= years * DAYS_IN_YEAR;

            month = MONTH[days];
            day = days - DAYS[month] + 1;
        }

        return formatDate(year, month, day, buffer, offset);
    }

    public static int formatDate(long timestamp, MutableBuffer buffer, int offset) {
        checkTimestamp(timestamp);

        int days = (int) ((timestamp - EPOCH_TO_2000_MS) / DAY_MS);
        int cycles4 = days / DAYS_IN_4_CYCLE;
        days -= cycles4 * DAYS_IN_4_CYCLE;

        int year = 2000 + (cycles4 << 2);
        int month;
        int day;

        if (days < DAYS_IN_LEAP_YEAR) {
            month = MONTH_LEAP[days];
            day = days - DAYS_LEAP[month] + 1;
        } else {
            days--;
            int years = days / DAYS_IN_YEAR;
            year += years;
            days -= years * DAYS_IN_YEAR;

            month = MONTH[days];
            day = days - DAYS[month] + 1;
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
        if (timestamp < EPOCH_TO_2000_MS | timestamp > EPOCH_TO_2100_MS) {
            throw new FormatterException("Timestamp " + timestamp + " is out of 2000-2100 years");
        }
    }

    protected static void checkTimestampNs(long timestamp) {
        if (timestamp < EPOCH_TO_2000_NS | timestamp > EPOCH_TO_2100_NS) {
            throw new FormatterException("Timestamp " + timestamp + " is out of 2000-2100 years");
        }
    }

    private static int daysToYear(int year) {
        year--;
        return year * DAYS_IN_YEAR + year / 4 - year / 100 + year / 400;
    }

}
