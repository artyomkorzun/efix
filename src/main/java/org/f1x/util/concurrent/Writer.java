package org.f1x.util.concurrent;

import org.f1x.util.buffer.MutableBuffer;

public interface Writer {

    void write(MutableBuffer buffer, int offset, int length);

}
