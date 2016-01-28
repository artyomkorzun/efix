package org.f1x.util.format;

import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.DateFormatter.formatDate;
import static org.f1x.util.format.TimeFormatter.formatTime;

public class TimestampFormatter {

    // TODO: optimize
    public static int formatTimestamp(long timestamp, MutableBuffer buffer, int offset) {
        offset = formatDate(timestamp, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = formatTime(timestamp, buffer, offset);
        return offset;
    }

}
