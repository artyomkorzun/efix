package org.efix.connector.channel;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

import static org.efix.util.TestUtil.byteMessage;

public class TextChannel implements Channel {

    protected final Queue<String> inQueue = new ArrayDeque<>();
    protected final Queue<String> outQueue = new ArrayDeque<>();

    public TextChannel(String... chunks) {
        Collections.addAll(inQueue, chunks);
    }

    @Override
    public int read(MutableBuffer buffer, int offset, int length) {
        if (inQueue.isEmpty())
            return 0;

        Buffer chunk = byteMessage(inQueue.remove());
        buffer.putBytes(offset, chunk, 0, chunk.capacity());

        return chunk.capacity();
    }

    @Override
    public int write(Buffer buffer, int offset, int length) {
        String chunk = BufferUtil.toString(buffer, offset, length);
        outQueue.add(chunk);
        return length;
    }

    public Queue<String> outQueue() {
        return outQueue;
    }

    public Queue<String> inQueue() {
        return inQueue;
    }

    public void clear() {
        inQueue.clear();
        outQueue.clear();
    }

}
