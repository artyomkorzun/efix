package org.efix.log.layout;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;


public interface Layout {

    int size(boolean inbound, long time, Buffer message, int offset, int length);

    void format(boolean inbound, long time, Buffer message, int offset, int length, MutableBuffer buffer, int bufferOffset);

}
