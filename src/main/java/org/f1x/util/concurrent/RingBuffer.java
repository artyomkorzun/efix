package org.f1x.util.concurrent;

import org.f1x.util.buffer.Buffer;

/**
 * Concurrent ring buffer.
 */
public interface RingBuffer {

    int capacity();

    int maxMessageLength();

    boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length);

    int read(MessageHandler messageHandler);

    int read(MessageHandler messageHandler, int messagesLimit);

}

