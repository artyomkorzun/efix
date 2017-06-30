package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.TimeType;

import static org.efix.util.parse.IntParser.parse2DigitUInt;
import static org.efix.util.parse.IntParser.parse3DigitUInt;
import static org.efix.util.parse.ParserUtil.*;


public class TimeParser {

    protected static int parseTime(int tag, Buffer buffer, int offset, int end) {
        int time = parseSecondTime(buffer, offset);
        time += parse3DigitUInt(buffer, offset + TimeType.MILLISECOND_OFFSET);
        return time;
    }


    /**
     * Parses time in format HH:MM:SS or HH:MM:SS.sss without leap seconds
     */
    public static int parseTime(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        int free = end - off;

        checkBounds(TimeType.SECOND_TIME_LENGTH + SEPARATOR_LENGTH, free);
        int time = parseSecondTime(buffer, off);

        byte b = buffer.getByte(off + TimeType.DOT_OFFSET);
        if (b == '.') {
            checkBounds(TimeType.MILLISECOND_TIME_LENGTH + SEPARATOR_LENGTH, free);
            time += parse3DigitUInt(buffer, off + TimeType.MILLISECOND_OFFSET);
            b = buffer.getByte(off + TimeType.MILLISECOND_TIME_LENGTH);
            off += TimeType.MILLISECOND_TIME_LENGTH + SEPARATOR_LENGTH;
        } else {
            off += TimeType.SECOND_TIME_LENGTH + SEPARATOR_LENGTH;
        }

        checkByte(b, separator);
        offset.set(off);

        return time;
    }

    protected static int parseSecondTime(Buffer buffer, int offset) {
        int hour = parse2DigitUInt(buffer, offset + TimeType.HOUR_OFFSET);
        checkHour(hour);
        checkByte(buffer.getByte(offset + TimeType.FIRST_COLON_OFFSET), (byte) ':');

        int minute = parse2DigitUInt(buffer, offset + TimeType.MINUTE_OFFSET);
        checkMinute(minute);
        checkByte(buffer.getByte(offset + TimeType.SECOND_COLON_OFFSET), (byte) ':');

        int second = parse2DigitUInt(buffer, offset + TimeType.SECOND_OFFSET);
        checkSecond(second);

        //return ((hour * 60 + minute) * 60 + second) * 1000;
        int minutes = (hour << 6) - (hour << 2) + minute;
        int seconds = (minutes << 6) - (minutes << 2) + second;
        return (seconds << 10) - ((seconds << 4) + (seconds << 3));
    }

    protected static int checkHour(int hour) {
        if (hour > TimeType.MAX_HOUR_VALUE)
            throw new ParserException("Invalid hour " + hour);

        return hour;
    }

    protected static int checkMinute(int minute) {
        if (minute > TimeType.MAX_MINUTE_VALUE)
            throw new ParserException("Invalid minute " + minute);

        return minute;
    }

    protected static int checkSecond(int second) {
        if (second > TimeType.MAX_SECOND_VALUE)
            throw new ParserException("Invalid second " + second);

        return second;
    }

}
