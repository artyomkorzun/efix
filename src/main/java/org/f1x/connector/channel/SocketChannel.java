package org.f1x.connector.channel;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SocketChannel implements Channel {

    protected final java.nio.channels.SocketChannel channel;

    public SocketChannel(java.nio.channels.SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public int read(MutableBuffer buffer, int offset, int length) throws IOException {
        ByteBuffer byteBuffer = buffer.byteBuffer();
        byteBuffer.limit(offset + length).position();
        return channel.read(byteBuffer, );
    }

    @Override
    public boolean write(Buffer buffer, int offset, int length) throws IOException {
        return false;
    }

    @Override
    public void close() {

    }

}
