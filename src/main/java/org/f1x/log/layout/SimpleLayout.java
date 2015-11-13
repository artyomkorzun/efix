package org.f1x.log.layout;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public class SimpleLayout implements Layout {

    protected static final byte LINE_SEPARATOR = '\n';
    protected static final int LINE_SEPARATOR_LENGTH = 1;

    @Override
    public int size(boolean inbound, long time, Buffer buffer, int offset, int length) {
        return length + LINE_SEPARATOR_LENGTH;
    }

    @Override
    public void format(boolean inbound, long time, Buffer srcBuffer, int srcOffset, MutableBuffer dstBuffer, int dstOffset, int length) {
        dstBuffer.putBytes(dstOffset, srcBuffer, srcOffset, length);
        dstBuffer.putByte(dstOffset + length, LINE_SEPARATOR);
    }

}
