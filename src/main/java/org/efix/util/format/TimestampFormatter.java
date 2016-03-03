package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;

import static org.efix.util.format.DateFormatter.formatDate;

public class TimestampFormatter {

    // TODO: optimize
    public static int formatTimestamp(long timestamp, MutableBuffer buffer, int offset) {
        offset = formatDate(timestamp, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = TimeFormatter.formatTime(timestamp, buffer, offset);
        return offset;
    }

}
