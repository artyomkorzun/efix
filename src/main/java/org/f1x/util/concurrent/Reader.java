package org.f1x.util.concurrent;


import org.f1x.util.buffer.Buffer;

public interface Reader {

    void read(int messageType, Buffer buffer, int offset, int length);

}
