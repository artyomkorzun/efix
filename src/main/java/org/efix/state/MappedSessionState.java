package org.efix.state;

import org.efix.util.BitUtil;
import org.efix.util.LangUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.util.Objects.requireNonNull;

public class MappedSessionState extends AbstractSessionState {

    protected static final int LENGTH = 2 * BitUtil.SIZE_OF_INT + BitUtil.SIZE_OF_LONG;

    protected static final int SENDER_SEQ_NUM_OFFSET = 0;
    protected static final int TARGET_SEQ_NUM_OFFSET = SENDER_SEQ_NUM_OFFSET + BitUtil.SIZE_OF_INT;
    protected static final int START_SESSION_TIME_OFFSET = TARGET_SEQ_NUM_OFFSET + BitUtil.SIZE_OF_INT;

    protected final Path path;

    protected MappedByteBuffer byteBuffer;
    protected MutableBuffer buffer;

    public MappedSessionState(Path path) {
        this.path = requireNonNull(path);
    }

    @Override
    public int senderSeqNum() {
        return buffer.getInt(SENDER_SEQ_NUM_OFFSET);
    }

    @Override
    public void senderSeqNum(int seqNum) {
        buffer.putInt(SENDER_SEQ_NUM_OFFSET, seqNum);
    }

    @Override
    public int targetSeqNum() {
        return buffer.getInt(TARGET_SEQ_NUM_OFFSET);
    }

    @Override
    public void targetSeqNum(int seqNum) {
        buffer.putInt(TARGET_SEQ_NUM_OFFSET, seqNum);
    }

    @Override
    public long sessionStartTime() {
        return buffer.getLong(START_SESSION_TIME_OFFSET);
    }

    @Override
    public void sessionStartTime(long time) {
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
            senderSeqNum(1);
            targetSeqNum(1);
            sessionStartTime(Long.MIN_VALUE);
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

}
