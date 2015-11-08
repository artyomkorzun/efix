package org.f1x.util.concurrent;

import org.f1x.util.buffer.Buffer;

/**
 * Concurrent ring buffer.
 */
public interface RingBuffer {

    int capacity();

    int maxMessageLength();

    boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length);

    boolean write(int messageType, Writer writer, int length);

    int read(Reader reader);

    int read(Reader reader, int messagesLimit);

}

