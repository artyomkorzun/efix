package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.TimeType;
import org.efix.util.type.TimestampType;

import static org.efix.util.parse.IntParser.parse3DigitUInt;
import static org.efix.util.parse.ParserUtil.checkByte;
import static org.efix.util.parse.ParserUtil.checkFreeSpace;

public class TimestampParser {

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        int free = end - off;

        checkFreeSpace(free, TimestampType.SECOND_TIMESTAMP_LENGTH + 1);
        long time = DateParser.parseDate(buffer, off);

        checkByte(buffer.getByte(off + TimestampType.DASH_OFFSET), (byte) '-');
        off += TimestampType.TIME_OFFSET;
        time += TimeParser.parseSecondTime(buffer, off);

        byte b = buffer.getByte(off + TimeType.DOT_OFFSET);
        if (b == '.') {
            checkFreeSpace(free, TimestampType.MILLISECOND_TIMESTAMP_LENGTH + 1);
            time += parse3DigitUInt(buffer, off + TimeType.MILLISECOND_OFFSET);
            b = buffer.getByte(off + TimeType.MILLISECOND_TIME_LENGTH);
            off += TimeType.MILLISECOND_TIME_LENGTH + 1;
        } else {
            off += TimeType.SECOND_TIME_LENGTH + 1;
        }

        checkByte(b, separator);
        offset.set(off);

        return time;
    }

}
