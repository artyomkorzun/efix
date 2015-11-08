package org.f1x.engine;

import org.f1x.util.buffer.Buffer;

public interface SessionEngine extends AutoCloseable {

    void start();

    void close();

    void sendAppMessage(Buffer buffer, int offset, int length);

}
