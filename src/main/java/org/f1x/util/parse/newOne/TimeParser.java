package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.checkDigit;
import static org.f1x.util.parse.newOne.ParserUtil.checkMinLength;

public class TimeParser {

    protected static final int DATE_LENGTH = 8;

    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        return 0;
    }

    public static int parseTime(byte separator, Buffer buffer, MutableInt offset, int end) {

        return 0;
    }

    public static long parseDate(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkMinLength(end - off, DATE_LENGTH + 1);

        long date = parseDate(buffer, off);
        offset.value(off + DATE_LENGTH + 1);

        return date;
    }

    protected static long parseDate(Buffer buffer, int offset) {
        int year = parseYear(buffer, offset);
        int month = parseMonth(buffer, offset + 4);
        int day = parseDay(buffer, offset + 6);

        //http://stackoverflow.com/questions/3220163/how-to-find-leap-year-programatically-in-c/11595914#11595914
        boolean leapYear = (year & 0b11) == 0 && ((year % 25) != 0 || (year & 0b1111) == 0);
        checkMonthAndDay(leapYear, month, day);

        return 0;
    }

    protected static int parseYear(Buffer buffer, int offset) {
        int year = checkDigit(buffer.getByte(offset));
        year = (year << 3) + (year << 1) + checkDigit(buffer.getByte(offset + 1));
        year = (year << 3) + (year << 1) + checkDigit(buffer.getByte(offset + 2));
        year = (year << 3) + (year << 1) + checkDigit(buffer.getByte(offset + 3));
        return year;
    }

    protected static int parseMonth(Buffer buffer, int offset) {
        int month = checkDigit(buffer.getByte(offset));
        month = (month << 3) + (month << 1) + checkDigit(buffer.getByte(offset + 1));
        return month;
    }

    protected static int parseDay(Buffer buffer, int offset) {
        int day = checkDigit(buffer.getByte(offset));
        day = (day << 3) + (day << 1) + checkDigit(buffer.getByte(offset + 1));
        return day;
    }

    protected static void checkMonthAndDay(boolean leapYear, int month, int day) {
        int max;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                max = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                max = 30;
                break;
            case 2:
                max = leapYear ? 29 : 28;
                break;
            default:
                throw new ParserException("invalid month " + month);
        }

        if (day < 1 || day > max)
            throw new ParserException(String.format("invalid day %s for month %s", day, month));
    }


}
