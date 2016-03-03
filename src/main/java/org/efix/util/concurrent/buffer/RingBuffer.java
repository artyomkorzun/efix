package org.efix.util.concurrent.buffer;

import org.efix.util.buffer.Buffer;

public interface RingBuffer {

    int capacity();

    int maxMessageLength();

    boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length);

    int read(MessageHandler handler);

    int size();

    boolean isEmpty();

}

