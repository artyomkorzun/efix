package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.IntParser.parse2DigitUInt;
import static org.f1x.util.parse.IntParser.parse3DigitUInt;
import static org.f1x.util.parse.ParserUtil.checkByte;
import static org.f1x.util.parse.ParserUtil.checkFreeSpace;

public class TimeParser {

    protected static final int SECOND_TIME_LENGTH = 8;
    protected static final int MILLISECOND_TIME_LENGTH = 12;

    protected static final int HOUR_OFFSET = 0;
    protected static final int FIRST_COLON_OFFSET = 2;
    protected static final int MINUTE_OFFSET = 3;
    protected static final int SECOND_COLON_OFFSET = 5;
    protected static final int SECOND_OFFSET = 6;
    protected static final int DOT_OFFSET = 8;
    protected static final int MILLISECOND_OFFSET = 9;

    /**
     * Parses time in format HH:MM:SS or HH:MM:SS.sss without leap seconds
     */
    public static int parseTime(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        int length = end - off;

        checkFreeSpace(length, SECOND_TIME_LENGTH + 1);
        int time = parseSecondTime(buffer, off);

        byte b = buffer.getByte(off + DOT_OFFSET);
        if (b == '.') {
            checkFreeSpace(length, MILLISECOND_TIME_LENGTH + 1);
            time += parse3DigitUInt(buffer, off + MILLISECOND_OFFSET);
            b = buffer.getByte(off + MILLISECOND_TIME_LENGTH);
            off += MILLISECOND_TIME_LENGTH + 1;
        } else {
            off += SECOND_TIME_LENGTH + 1;
        }

        checkByte(b, separator);
        offset.value(off);

        return time;
    }

    protected static int parseSecondTime(Buffer buffer, int offset) {
        int hour = parse2DigitUInt(buffer, offset + HOUR_OFFSET);
        checkHour(hour);
        checkByte(buffer.getByte(offset + FIRST_COLON_OFFSET), (byte) ':');

        int minute = parse2DigitUInt(buffer, offset + MINUTE_OFFSET);
        checkMinute(minute);
        checkByte(buffer.getByte(offset + SECOND_COLON_OFFSET), (byte) ':');

        int second = parse2DigitUInt(buffer, offset + SECOND_OFFSET);
        checkSecond(second);

        //return ((hour * 60 + minute) * 60 + second) * 1000;
        int minutes = (hour << 6) - (hour << 2) + minute;
        int seconds = (minutes << 6) - (minutes << 2) + second;
        return (seconds << 10) - ((seconds << 4) + (seconds << 3));
    }

    protected static int checkHour(int hour) {
        if (hour > 23)
            throw new ParserException("invalid hour " + hour);

        return hour;
    }

    protected static int checkMinute(int minute) {
        if (minute > 59)
            throw new ParserException("invalid minute " + minute);

        return minute;
    }

    protected static int checkSecond(int second) {
        if (second > 59)
            throw new ParserException("invalid second " + second);

        return second;
    }

}
