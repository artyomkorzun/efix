package org.efix.log.layout;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.format.ByteFormatter;
import org.efix.util.format.TimestampFormatter;
import org.efix.util.type.TimestampType;

import static org.efix.util.format.CharFormatter.formatChar;


public class TimeLayout implements Layout {

    protected static final int LAYOUT_LENGTH = TimestampType.MILLISECOND_TIMESTAMP_LENGTH + 3;

    @Override
    public int size(boolean inbound, long time, Buffer message, int offset, int length) {
        return length + LAYOUT_LENGTH;
    }

    @Override
    public void format(boolean inbound, long time, Buffer message, int offset, int length, MutableBuffer buffer, int bufferOffset) {
        bufferOffset = TimestampFormatter.formatTimestampMs(time, buffer, bufferOffset);
        bufferOffset = formatChar(':', buffer, bufferOffset);
        bufferOffset = formatChar(' ', buffer, bufferOffset);
        bufferOffset = ByteFormatter.formatBytes(message, offset, length, buffer, bufferOffset);
        bufferOffset = formatChar('\n', buffer, bufferOffset);
    }

}
