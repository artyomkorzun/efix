package org.f1x.util;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

public class ByteSequenceWrapper implements ByteSequence {

    protected static final byte[] EMPTY = new byte[0];

    protected final MutableBuffer buffer = new UnsafeBuffer(EMPTY);

    public ByteSequenceWrapper() {
    }

    public ByteSequenceWrapper(Buffer buffer, int offset, int length) {
        wrap(buffer, offset, length);
    }

    public ByteSequenceWrapper(Buffer buffer) {
        wrap(buffer);
    }

    public ByteSequenceWrapper wrap(Buffer buffer) {
        this.buffer.wrap(buffer, 0, buffer.capacity());
        return this;
    }

    public ByteSequenceWrapper wrap(Buffer buffer, int offset, int length) {
        this.buffer.wrap(buffer, offset, length);
        return this;
    }

    public Buffer buffer() {
        return buffer;
    }

    @Override
    public int length() {
        return buffer.capacity();
    }

    @Override
    public byte byteAt(int index) {
        return buffer.getByte(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        ByteSequenceWrapper sequence = new ByteSequenceWrapper();
        sequence.wrap(buffer, start, end - start);
        return sequence;
    }

    public ByteSequenceWrapper clear() {
        buffer.wrap(EMPTY);
        return this;
    }

    @Override
    public int hashCode() {
        return buffer != null ? buffer.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringBuilder(this).toString();
    }

    public static ByteSequenceWrapper of(String string) {
        return new ByteSequenceWrapper(BufferUtil.fromString(string));
    }

}
