package org.f1x.util;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

public class ByteSequence implements CharSequence {

    protected static final byte[] EMPTY = new byte[0];

    protected MutableBuffer wrapper = new UnsafeBuffer(EMPTY);

    public ByteSequence wrap(Buffer buffer, int offset, int length) {
        wrapper.wrap(buffer, offset, length);
        return this;
    }

    public ByteSequence wrap(Buffer buffer) {
        wrapper.wrap(buffer, 0, buffer.capacity());
        return this;
    }

    public Buffer wrapper() {
        return wrapper;
    }

    @Override
    public int length() {
        return wrapper.capacity();
    }

    @Override
    public char charAt(int index) {
        return (char) wrapper.getByte(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        ByteSequence sequence = new ByteSequence();
        sequence.wrap(wrapper, start, end - start);
        return sequence;
    }

    @Override
    public final String toString() {
        return new StringBuilder(this).toString();
    }

    public ByteSequence clear() {
        wrapper.wrap(EMPTY);
        return this;
    }

}
