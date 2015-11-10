package org.f1x.engine;

import org.f1x.util.buffer.Buffer;

public interface SessionEngine extends AutoCloseable {

    void start();

    void close();

    void sendMessage(Buffer buffer, int offset, int length);

}
