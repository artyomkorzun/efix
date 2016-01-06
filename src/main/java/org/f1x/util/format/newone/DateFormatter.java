package org.f1x.util.format.newone;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.newone.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.newone.IntFormatter.format2DigitUInt;
import static org.f1x.util.format.newone.IntFormatter.format4DigitUInt;

public class DateFormatter {

    protected static final int DATE_LENGTH = 8;
    protected static final int YEAR_OFFSET = 0;
    protected static final int MONTH_OFFSET = 4;
    protected static final int DAY_OFFSET = 6;

    protected static final int DAYS_TO_EPOCH = 1969 * 365 + 1969 / 4 - 1969 / 100 + 1969 / 400;
    protected static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    protected static final int DAYS_IN_YEAR = 365;

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

    private static final int[] DAYS_TO_MONTH = new int[]{0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final int[] DAYS_TO_MONTH_LEAP = new int[]{0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    public static void formatDate(long timestamp, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, DATE_LENGTH);
        formatDate(timestamp, buffer, off);
        offset.value(off + DATE_LENGTH);
    }

    protected static void formatDate(long timestamp, MutableBuffer buffer, int offset) {
        int days = (int) (timestamp / DAY_IN_MILLIS + DAYS_TO_EPOCH);
        int year = (int) ((timestamp + DAYS_TO_EPOCH * DAY_IN_MILLIS) / (6L * 6 * 6 * 1000 * 365 * 97));

        int years4 = year >> 2;          // year / 4
        int years100 = years4 / 25;      // year / 100
        int years400 = years100 >> 2;    // year / 400

        int daysToYear = DAYS_IN_YEAR * year + years4 - years100 + years400;   // 365 * year + year / 4 - year / 100 + year / 400
        int daysOfYear = days - daysToYear;
        boolean leapYear = (year & 0b11) == 0 && (year - ((years100 << 6) + (years100 << 5) + (years100 << 2)) != 0 || (year & 0b1111) == 0); // year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

        int month;
        int day;
        if (leapYear) {
            month = MONTH_LEAP[daysOfYear];
            day = daysOfYear - DAYS_TO_MONTH_LEAP[month] + 1;
        } else {
            month = MONTH[daysOfYear];
            day = daysOfYear - DAYS_TO_MONTH[month] + 1;
        }

        year++;

        format4DigitUInt(year, buffer, offset + YEAR_OFFSET);
        format2DigitUInt(month, buffer, offset + MONTH_OFFSET);
        format2DigitUInt(day, buffer, offset + DAY_OFFSET);
    }

}
