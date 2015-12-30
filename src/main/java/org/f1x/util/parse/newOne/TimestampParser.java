package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.IntParser.parse3DigitInt;
import static org.f1x.util.parse.newOne.ParserUtil.checkByte;
import static org.f1x.util.parse.newOne.ParserUtil.checkMinLength;

public class TimestampParser {

    protected static final byte DASH = '-';

    protected static final int SECOND_TIMESTAMP_LENGTH = DateParser.DATE_LENGTH + 1 + TimeParser.SECOND_TIME_LENGTH;
    protected static final int MILLISECOND_TIMESTAMP_LENGTH = DateParser.DATE_LENGTH + 1 + TimeParser.MILLISECOND_TIME_LENGTH;

    protected static final int TIME_OFFSET = DateParser.DATE_LENGTH + 1;
    protected static final int DASH_OFFSET = DateParser.DATE_LENGTH;

    /**
     * Parses timestamp in format YYYYMMDD-HH:MM:SS or YYYYMMDD-HH:MM:SS.sss. Supports leap year. Doesn't support leap second.
     */
    public static long parseTimestamp(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        int length = end - off;

        checkMinLength(length, SECOND_TIMESTAMP_LENGTH + 1);
        long time = DateParser.parseDate(buffer, off);

        checkByte(buffer.getByte(off + DASH_OFFSET), DASH);
        off += TIME_OFFSET;
        time += TimeParser.parseSecondTime(buffer, off);

        byte b = buffer.getByte(off + TimeParser.DOT_OFFSET);
        if (b == '.') {
            checkMinLength(length, MILLISECOND_TIMESTAMP_LENGTH + 1);
            time += parse3DigitInt(buffer, off + TimeParser.MILLISECOND_OFFSET);
            b = buffer.getByte(off + TimeParser.MILLISECOND_TIME_LENGTH);
            off += TimeParser.MILLISECOND_TIME_LENGTH + 1;
        } else {
            off += TimeParser.SECOND_TIME_LENGTH + 1;
        }

        checkByte(b, separator);
        offset.value(off);

        return time;
    }

}
