package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;


public class TimestampFormatter {

    public static int formatTimestampNs(final long timestampNs, final MutableBuffer buffer, int offset) {
        offset = DateFormatter.formatDateNs(timestampNs, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = TimeFormatter.formatTimeNs(timestampNs, buffer, offset);
        return offset;
    }

    public static int formatTimestampMs(final long timestampMs, final MutableBuffer buffer, int offset) {
        offset = DateFormatter.formatDate(timestampMs, buffer, offset);
        buffer.putByte(offset++, (byte) '-');
        offset = TimeFormatter.formatTimeMs(timestampMs, buffer, offset);
        return offset;
    }

}
