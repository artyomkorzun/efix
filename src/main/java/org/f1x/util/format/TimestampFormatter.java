package org.f1x.util.format;

import org.f1x.message.fields.type.TimestampType;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.DateFormatter.formatDate;
import static org.f1x.util.format.FormatterUtil.checkFreeSpace;
import static org.f1x.util.format.TimeFormatter.formatTime;

public class TimestampFormatter {

    // TODO: optimize
    public static void formatTimestamp(long timestamp, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, TimestampType.MILLISECOND_TIMESTAMP_LENGTH);

        formatDate(timestamp, buffer, off);
        buffer.putByte(off + TimestampType.DASH_OFFSET, (byte) '-');
        formatTime(timestamp, buffer, off + TimestampType.DASH_OFFSET + 1);

        offset.value(off + TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
    }

}
