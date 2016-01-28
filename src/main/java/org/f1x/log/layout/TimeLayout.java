package org.f1x.log.layout;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.TimestampType;

import static org.f1x.util.format.ByteFormatter.formatBytes;
import static org.f1x.util.format.CharFormatter.formatChar;
import static org.f1x.util.format.TimestampFormatter.formatTimestamp;


public class TimeLayout implements Layout {

    protected static final int LAYOUT_LENGTH = TimestampType.MILLISECOND_TIMESTAMP_LENGTH + 3;

    @Override
    public int size(boolean inbound, long time, Buffer buffer, int offset, int length) {
        return length + LAYOUT_LENGTH;
    }

    @Override
    public void format(boolean inbound, long time, Buffer srcBuffer, int srcOffset, MutableBuffer dstBuffer, int dstOffset, int length) {
        int offset = dstOffset;
        offset = formatTimestamp(time, dstBuffer, offset);
        offset = formatChar(':', dstBuffer, offset);
        offset = formatChar(' ', dstBuffer, offset);
        offset = formatBytes(srcBuffer, srcOffset, length, dstBuffer, offset);
        offset = formatChar('\n', dstBuffer, offset);
    }

}
