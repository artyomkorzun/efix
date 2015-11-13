package org.f1x.log.layout;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public interface Layout {

    int size(boolean inbound, long time, Buffer buffer, int offset, int length);

    void format(boolean inbound, long time, Buffer srcBuffer, int srcOffset, MutableBuffer dstBuffer, int dstOffset, int length);

}
