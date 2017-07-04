package org.efix.util.parse;

import org.efix.message.FieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.TimeType;

import static org.efix.util.parse.IntParser.parse2DigitUInt;
import static org.efix.util.parse.IntParser.parse3DigitUInt;
import static org.efix.util.parse.ParserUtil.SEPARATOR_LENGTH;
import static org.efix.util.parse.ParserUtil.checkBounds;


public class TimeParser {

    public static long parseTime(int tag, Buffer buffer, int offset, int end) {
        int length = end - offset;
        if (length < TimeType.SECOND_TIME_LENGTH) {
            throw new FieldException(tag, "Not valid time");
        }

        int seconds = parseSeconds(tag, buffer, offset);
        int milliseconds = 0;

        if (length == TimeType.MILLISECOND_TIME_LENGTH) {
            checkByte(tag, '.', buffer, offset + TimeType.DOT_OFFSET);
            milliseconds = parse3DigitUInt(buffer, offset + TimeType.MILLISECOND_OFFSET);
        } else if (length != TimeType.SECOND_TIME_LENGTH) {
            throw new FieldException(tag, "Not valid time");
        }

        return 1000 * seconds + milliseconds;
    }

    private static int parseSeconds(int tag, Buffer buffer, int offset) {
        int hour = parseHour(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimeType.FIRST_COLON_OFFSET);

        int minute = parseMinute(tag, buffer, offset);
        checkByte(tag, ':', buffer, offset + TimeType.SECOND_COLON_OFFSET);

        int second = parseSecond(tag, buffer, offset);

        return 3600 * hour + 60 * minute + second;
    }

    private static int parseHour(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimeType.HOUR_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimeType.HOUR_OFFSET + 1);

        if (b1 < '0' | b1 > '2' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid time");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMinute(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimeType.MINUTE_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimeType.MINUTE_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid time");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseSecond(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimeType.SECOND_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimeType.SECOND_OFFSET + 1);

        if (b1 < '0' | b1 > '5' | b2 < '0' | b2 > '9') {
            throw new FieldException(tag, "Not valid time");
        }

        return 10 * (b1 - '0') + (b2 - '0');
    }

    private static int parseMilliseconds(int tag, Buffer buffer, int offset) {
        byte b1 = buffer.getByte(offset + TimeType.MILLISECOND_OFFSET + 0);
        byte b2 = buffer.getByte(offset + TimeType.MILLISECOND_OFFSET + 1);
        byte b3 = buffer.getByte(offset + TimeType.MILLISECOND_OFFSET + 2);

        if (b1 < '0' | b1 > '9' | b2 < '0' | b2 > '9' | b3 < '0' | b3 > '9') {
            throw new FieldException(tag, "Not valid time");
        }

        return 100 * (b1 - '0') + 10 * (b2 - '0') + (b3 - '0');
    }

    private static void checkByte(int tag, char expected, Buffer buffer, int offset) {
        byte b = buffer.getByte(offset);
        if (b != expected) {
            throw new FieldException(tag, "Not valid time");
        }
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

        ParserUtil.checkByte(b, separator);
        offset.set(off);

        return time;
    }

    protected static int parseSecondTime(Buffer buffer, int offset) {
        int hour = parse2DigitUInt(buffer, offset + TimeType.HOUR_OFFSET);
        checkHour(hour);
        ParserUtil.checkByte(buffer.getByte(offset + TimeType.FIRST_COLON_OFFSET), (byte) ':');

        int minute = parse2DigitUInt(buffer, offset + TimeType.MINUTE_OFFSET);
        checkMinute(minute);
        ParserUtil.checkByte(buffer.getByte(offset + TimeType.SECOND_COLON_OFFSET), (byte) ':');

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
