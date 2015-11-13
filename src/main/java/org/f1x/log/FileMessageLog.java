package org.f1x.log;

import org.f1x.log.layout.Layout;
import org.f1x.util.CloseHelper;
import org.f1x.util.LangUtil;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileMessageLog implements MessageLog {

    protected final Layout layout;
    protected final Path path;
    protected final int bufferSize;

    protected FileChannel channel;
    protected ByteBuffer byteBuffer;
    protected MutableBuffer buffer;

    public FileMessageLog(int bufferSize, Path path, Layout layout) {
        this.bufferSize = checkBufferSize(bufferSize);
        this.path = checkPath(path);
        this.layout = layout;
    }

    @Override
    public void log(boolean inbound, long time, Buffer buffer, int offset, int length) {
        int size = layout.size(inbound, time, buffer, offset, length);
        checkEntrySize(size);

        if (byteBuffer.remaining() < size)
            flush();

        int position = byteBuffer.position();
        layout.format(inbound, time, buffer, offset, this.buffer, position, length);
        byteBuffer.position(position + size);
    }

    @Override
    public void open() {
        try {
            channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            byteBuffer = ByteBuffer.allocateDirect(bufferSize);
            buffer = new UnsafeBuffer(byteBuffer);
        } catch (Exception e) {
            LangUtil.rethrowUnchecked(e);
        }
    }

    @Override
    public void flush() {
        if (byteBuffer.position() > 0) {
            try {
                byteBuffer.flip();
                write(byteBuffer, channel);
            } finally {
                byteBuffer.clear();
            }
        }
    }

    @Override
    public void close() {
        try {
            flush();
        } finally {
            byteBuffer = null;
            buffer = null;
            FileChannel closeable = channel;
            channel = null;
            CloseHelper.close(closeable);
        }
    }

    protected void write(Buffer buffer, int offset, int length, FileChannel channel) {
        ByteBuffer byteBuffer = buffer.byteBuffer();
        int position = buffer.offset() + offset;
        int limit = position + length;
        byteBuffer.limit(limit).position(position);
        write(byteBuffer, channel);
    }

    protected void write(ByteBuffer byteBuffer, FileChannel channel) {
        try {
            channel.write(byteBuffer);
        } catch (IOException e) {
            LangUtil.rethrowUnchecked(e);
        }
    }

    protected void checkEntrySize(int size) {
        int capacity = byteBuffer.capacity();
        if (size > capacity)
            throw new IllegalArgumentException(String.format("Entry size %s more than buffer capacity %s", size, capacity));
    }

    protected int checkBufferSize(int bufferSize) {
        if (bufferSize <= 0)
            throw new IllegalArgumentException("Non positive buffer size " + bufferSize);

        return bufferSize;
    }

    protected Path checkPath(Path path) {
        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("Not file " + path);

        return path;
    }

}
