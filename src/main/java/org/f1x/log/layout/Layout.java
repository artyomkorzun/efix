package org.f1x.log.layout;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;


public interface Layout {

    int size(boolean inbound, long time, Buffer message, int offset, int length);

    void format(boolean inbound, long time, Buffer message, int offset, int length, MutableBuffer buffer, int bufferOffset);

}
