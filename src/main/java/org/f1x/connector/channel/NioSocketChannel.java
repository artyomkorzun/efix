package org.f1x.connector.channel;

import org.f1x.connector.ConnectionException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioSocketChannel implements Channel {

    protected final SocketChannel channel;

    public NioSocketChannel(java.nio.channels.SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public int read(MutableBuffer buffer, int offset, int length) {
        ByteBuffer byteBuffer = buffer.byteBuffer();
        byteBuffer.limit(offset + length).position(offset);
        try {
            return channel.read(byteBuffer);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public int write(Buffer buffer, int offset, int length) {
        ByteBuffer byteBuffer = buffer.byteBuffer();
        byteBuffer.limit(offset + length).position(offset);
        try {
            return channel.write(byteBuffer);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

}
