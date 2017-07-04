package org.efix.util.parse;

import org.efix.message.FieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DateType;

import static org.efix.util.parse.IntParser.parse2DigitUInt;
import static org.efix.util.parse.IntParser.parse4DigitUInt;
import static org.efix.util.parse.ParserUtil.*;


public class DateParser {

    protected static final int DAYS_TO_EPOCH = 1969 * 365 + 1969 / 4 - 1969 / 100 + 1969 / 400;
    protected static final long DAY_MS = 24 * 60 * 60 * 1000;
    protected static final int DAYS_IN_YEAR = 365;

    private static final byte[] MONTH_TO_DAYS = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final byte[] MONTH_TO_DAYS_LEAP = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final short[] DAYS_TO_NEW_YEAR = {0, 365, 334, 306, 275, 245, 214, 184, 153, 122, 92, 61, 31};
    private static final short[] DAYS_TO_NEW_YEAR_LEAP = {0, 366, 335, 306, 275, 245, 214, 184, 153, 122, 92, 61, 31};


    private static final int DAYS_TO_2000 = 1999 * 365 + 1999 / 4 - 1999 / 100 + 1999 / 400;
    private static final int DAYS_EPOCH_TO_2000 = DAYS_TO_2000 - DAYS_TO_EPOCH;

    private static final short[] DAYS_FROM_NEW_YEAR = {1, 32, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    private static final short[] DAYS_FROM_NEW_YEAR_LEAP = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};

    public static long parseDate(int tag, Buffer buffer, int offset, int end) {
        if (offset + DateType.LENGTH != end) {
            throw new FieldException(tag, "Not valid date");
        }

        int days = parseDays(tag, buffer, offset);
        return days * DAY_MS;
    }

    private static int parseDays(int tag, Buffer buffer, int offset) {
        int year = parseYear(tag, buffer, offset + DateType.YEAR_OFFSET);
        int month = parseMonth(tag, buffer, offset + DateType.MONTH_OFFSET);
        int day = parseDay(tag, buffer, offset + DateType.DAY_OFFSET);

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
            throw new FieldException(tag, "Not valid date");
        }

        return 10 * (b3 - '0') + (b4 - '0');
    }

    private static int parseMonth(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset);
        byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '1' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid date");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // month - 1
    }

    private static int parseDay(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset);
        byte b2 = buffer.getByte(offset + 1);

        if (b1 < '0' | b1 > '3' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid date");
        }

        return 10 * (b1 - '0') + (b2 - '1'); // day - 1
    }

    public static long parseDate(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();

        checkBounds(DateType.LENGTH + SEPARATOR_LENGTH, end - off);
        long time = parseDate(buffer, off);

        checkByte(buffer.getByte(off + DateType.LENGTH), separator);
        offset.set(off + DateType.LENGTH + SEPARATOR_LENGTH);

        return time;
    }

    protected static long parseDate(Buffer buffer, int offset) {
        int year = parse4DigitUInt(buffer, offset + DateType.YEAR_OFFSET);
        int month = parse2DigitUInt(buffer, offset + DateType.MONTH_OFFSET);
        int day = parse2DigitUInt(buffer, offset + DateType.DAY_OFFSET);

        checkMonth(month);

        int cycles4 = year >> 2;          // year / 4
        int cycles100 = cycles4 / 25;      // year / 100
        int cycles400 = cycles100 >> 2;    // year / 400

        int days = DAYS_IN_YEAR * year + cycles4 - cycles100 + cycles400;   // 365 * year + year / 4 - year / 100 + year / 400
        boolean leapYear = (year & 0b11) == 0 && (year - ((cycles100 << 6) + (cycles100 << 5) + (cycles100 << 2)) != 0 || (year & 0b1111) == 0); // year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

        int daysInMonth;
        int daysToNewYear;
        if (leapYear) {
            daysInMonth = MONTH_TO_DAYS_LEAP[month];
            daysToNewYear = DAYS_TO_NEW_YEAR_LEAP[month];
        } else {
            daysInMonth = MONTH_TO_DAYS[month];
            daysToNewYear = DAYS_TO_NEW_YEAR[month];
        }

        checkDay(day, daysInMonth);
        days += day - 1 - daysToNewYear - DAYS_TO_EPOCH;

        return days * DAY_MS;
    }

    private static int checkDay(int day, int daysInMonth) {
        if (day == 0 || day > daysInMonth)
            throw new ParserException("Invalid day " + day);

        return day;
    }

    protected static int checkMonth(int month) {
        if (month == 0 || month > 12)
            throw new ParserException("Invalid month " + month);

        return month;
    }

}
