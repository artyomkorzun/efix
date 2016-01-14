package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.FormatterUtil.checkFreeSpace;

public class TimestampFormatter {

    public static final int TIMESTAMP_LENGTH = DateFormatter.DATE_LENGTH + 1 + TimeFormatter.TIME_LENGTH;

    public static final int DASH_OFFSET = DateFormatter.DATE_LENGTH;

    // TODO: optimize
    public static void formatTimestamp(long timestamp, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, TIMESTAMP_LENGTH);

        DateFormatter.formatDate(timestamp, buffer, off);
        buffer.putByte(off + DASH_OFFSET, (byte) '-');
        TimeFormatter.formatTime(timestamp, buffer, off + DASH_OFFSET + 1);

        offset.value(off + TIMESTAMP_LENGTH);
    }

}
