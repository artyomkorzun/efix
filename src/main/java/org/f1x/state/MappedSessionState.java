package org.f1x.state;

import org.f1x.util.BitUtil;
import org.f1x.util.LangUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MappedSessionState extends AbstractSessionState {

    protected static final int LENGTH = 2 * BitUtil.SIZE_OF_INT + BitUtil.SIZE_OF_LONG;

    protected static final int SENDER_SEQ_NUM_OFFSET = 0;
    protected static final int TARGET_SEQ_NUM_OFFSET = SENDER_SEQ_NUM_OFFSET + BitUtil.SIZE_OF_INT;
    protected static final int START_SESSION_TIME_OFFSET = TARGET_SEQ_NUM_OFFSET + BitUtil.SIZE_OF_INT;

    protected final Path path;

    protected MappedByteBuffer byteBuffer;
    protected MutableBuffer buffer;

    public MappedSessionState(Path path) {
        this.path = checkFile(path);
    }

    @Override
    public int getNextSenderSeqNum() {
        return buffer.getInt(SENDER_SEQ_NUM_OFFSET);
    }

    @Override
    public void setNextSenderSeqNum(int newValue) {
        buffer.putInt(SENDER_SEQ_NUM_OFFSET, newValue);
    }

    @Override
    public int getNextTargetSeqNum() {
        return buffer.getInt(TARGET_SEQ_NUM_OFFSET);
    }

    @Override
    public void setNextTargetSeqNum(int newValue) {
        buffer.putInt(TARGET_SEQ_NUM_OFFSET, newValue);
    }

    @Override
    public long getSessionStartTime() {
        return buffer.getLong(START_SESSION_TIME_OFFSET);
    }

    @Override
    public void setSessionStartTime(long time) {
        buffer.putLong(START_SESSION_TIME_OFFSET, time);
    }

    @Override
    public void open() {
        boolean justCreated = !Files.exists(path);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, LENGTH).load();
            buffer = new UnsafeBuffer(byteBuffer);
        } catch (IOException e) {
            LangUtil.rethrowUnchecked(e);
        }

        if (justCreated) {
            setNextSenderSeqNum(1);
            setNextTargetSeqNum(1);
            setSessionStartTime(Long.MIN_VALUE);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
        try {
            byteBuffer.force();
        } finally {
            byteBuffer = null;
            buffer = null;
        }
    }

    protected Path checkFile(Path filePath) {
        if (!Files.isRegularFile(filePath))
            throw new IllegalArgumentException("Not file: " + filePath);

        return filePath;
    }

}
