package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.ParserUtil.checkByte;
import static org.f1x.util.parse.ParserUtil.checkFreeSpace;

public class DateParser {

    protected static final int DATE_LENGTH = 8;

    protected static final int YEAR_OFFSET = 0;
    protected static final int MONTH_OFFSET = 4;
    protected static final int DAY_OFFSET = 6;

    protected static final int DAYS_TO_EPOCH = 1969 * 365 + 1969 / 4 - 1969 / 100 + 1969 / 400;
    protected static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    protected static final int DAYS_IN_YEAR = 365;

    private static final int[] DAYS_IN_MONTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] DAYS_IN_MONTH_LEAP = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final int[] DAYS_TO_NEW_YEAR = {0, 365, 334, 306, 275, 245, 214, 184, 153, 122, 92, 61, 31};
    private static final int[] DAYS_TO_NEW_YEAR_LEAP = {0, 366, 335, 306, 275, 245, 214, 184, 153, 122, 92, 61, 31};

    public static long parseDate(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();

        checkFreeSpace(end - off, DATE_LENGTH + 1);
        long time = parseDate(buffer, off);

        checkByte(buffer.getByte(off + DATE_LENGTH), separator);
        offset.value(off + DATE_LENGTH + 1);

        return time;
    }

    protected static long parseDate(Buffer buffer, int offset) {
        int year = IntParser.parse4DigitInt(buffer, offset + YEAR_OFFSET);
        int month = IntParser.parse2DigitInt(buffer, offset + MONTH_OFFSET);
        int day = IntParser.parse2DigitInt(buffer, offset + DAY_OFFSET);

        checkMonth(month);

        int years4 = year >> 2;          // year / 4
        int years100 = years4 / 25;      // year / 100
        int years400 = years100 >> 2;    // year / 400

        int days = DAYS_IN_YEAR * year + years4 - years100 + years400;   // 365 * year + year / 4 - year / 100 + year / 400
        boolean leapYear = (year & 0b11) == 0 && (year - ((years100 << 6) + (years100 << 5) + (years100 << 2)) != 0 || (year & 0b1111) == 0); // year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

        int daysInMonth;
        int daysToNewYear;
        if (leapYear) {
            daysInMonth = DAYS_IN_MONTH_LEAP[month];
            daysToNewYear = DAYS_TO_NEW_YEAR_LEAP[month];
        } else {
            daysInMonth = DAYS_IN_MONTH[month];
            daysToNewYear = DAYS_TO_NEW_YEAR[month];
        }

        checkDay(day, daysInMonth);
        days += day - 1 - daysToNewYear - DAYS_TO_EPOCH;

        return days * DAY_IN_MILLIS; // TODO: optimize multiplication
    }

    private static int checkDay(int day, int daysInMonth) {
        if (day == 0 || day > daysInMonth)
            throw new ParserException("invalid day " + day);

        return day;
    }

    protected static int checkMonth(int month) {
        if (month == 0 || month > 12)
            throw new ParserException("invalid month " + month);

        return month;
    }

}
