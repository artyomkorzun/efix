package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;


public class TimestampParser {

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        long date = DateParser.parseDate((byte) '-', buffer, offset, end);
        int time = TimeParser.parseTime(separator, buffer, offset, end);
        return date + time;
    }

}
