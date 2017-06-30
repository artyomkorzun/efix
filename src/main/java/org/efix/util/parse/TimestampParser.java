package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.DateType;


public class TimestampParser {

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss(ssssss). Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(int tag, Buffer buffer, int offset, int end) {
        long date = DateParser.parseDate(buffer, offset);
        int time = TimeParser.parseTime(tag, buffer, offset + DateType.LENGTH + 1, end);
        return date + time;
    }

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        long date = DateParser.parseDate((byte) '-', buffer, offset, end);
        int time = TimeParser.parseTime(separator, buffer, offset, end);
        return date + time;
    }

}
