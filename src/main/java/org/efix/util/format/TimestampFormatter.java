package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;

import java.util.concurrent.TimeUnit;


public class TimestampFormatter {

    public static int formatTimestamp(long timestamp, TimeUnit unit, MutableBuffer buffer, int offset) {
        offset = DateFormatter.formatDate(timestamp, unit, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = TimeFormatter.formatTime(timestamp, unit, buffer, offset);
        return offset;
    }

    public static int formatTimestamp(long timestamp, MutableBuffer buffer, int offset) {
        offset = DateFormatter.formatDate(timestamp, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = TimeFormatter.formatTime(timestamp, buffer, offset);
        return offset;
    }

}
