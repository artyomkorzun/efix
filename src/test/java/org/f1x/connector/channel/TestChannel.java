package org.f1x.connector.channel;

import org.f1x.connector.ConnectionException;
import org.f1x.util.TestUtil;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public class TestChannel implements Channel {

    protected final Buffer[] chunks;

    protected int cursor;

    public TestChannel(String... chunks) {
        this.chunks = chunks(chunks);
    }

    @Override
    public int read(MutableBuffer buffer, int offset, int length) throws ConnectionException {
        if (cursor >= chunks.length)
            return -1;

        Buffer chunk = chunks[cursor++];
        int chunkLength = chunk.capacity();
        buffer.putBytes(offset, chunk, 0, chunkLength);
        return chunkLength;
    }

    @Override
    public int write(Buffer buffer, int offset, int length) throws ConnectionException {
        throw new UnsupportedOperationException();
    }

    protected static Buffer[] chunks(String... chunks) {
        Buffer[] array = new Buffer[chunks.length];
        for (int i = 0; i < chunks.length; i++)
            array[i] = TestUtil.byteMessage(chunks[i]);

        return array;
    }

}
