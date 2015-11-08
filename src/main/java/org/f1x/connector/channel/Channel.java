package org.f1x.connector.channel;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import java.io.IOException;

public interface Channel extends AutoCloseable {

    int read(MutableBuffer buffer, int offset, int length) throws IOException;

    boolean write(Buffer buffer, int offset, int length) throws IOException;

    void close();
}
