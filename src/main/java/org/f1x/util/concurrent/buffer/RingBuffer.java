package org.f1x.util.concurrent.buffer;

import org.f1x.util.buffer.Buffer;

public interface RingBuffer {

    int capacity();

    int maxMessageLength();

    boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length);

    int read(MessageHandler handler);

    int size();

    boolean isEmpty();

}

