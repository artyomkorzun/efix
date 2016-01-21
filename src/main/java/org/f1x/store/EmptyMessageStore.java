package org.f1x.store;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

public final class EmptyMessageStore implements MessageStore {

    public static final EmptyMessageStore INSTANCE = new EmptyMessageStore();

    private EmptyMessageStore() {
    }

    @Override
    public void write(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length) {
    }

    @Override
    public void read(int seqNum, Visitor visitor) {
    }

    @Override
    public void read(int fromSeqNum, int toSeqNum, Visitor visitor) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void open() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

}
