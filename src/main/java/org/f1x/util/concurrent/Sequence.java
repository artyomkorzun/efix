package org.f1x.util.concurrent;

import org.f1x.util.BitUtil;
import org.f1x.util.buffer.AtomicBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

public final class Sequence {

    private static final int LENGTH_WITH_PADDING = (BitUtil.CACHE_LINE_LENGTH_PROP_KEY << 1) - BitUtil.SIZE_OF_LONG;
    private static final int VALUE_OFFSET = BitUtil.CACHE_LINE_LENGTH_PROP_KEY - BitUtil.SIZE_OF_LONG;
    private static final int VALUE_INDEX = 0;

    private final AtomicBuffer buffer;

    public Sequence(long defaultValue) {
        this.buffer = createBuffer(defaultValue);
    }

    public long get() {
        return buffer.getLong(VALUE_INDEX);
    }

    public long getVolatile() {
        return buffer.getLongVolatile(VALUE_INDEX);
    }

    public void setOrdered(long value) {
        buffer.putLongOrdered(VALUE_INDEX, value);
    }

    public boolean compareAndSwap(long expected, long value) {
        return buffer.compareAndSetLong(VALUE_INDEX, expected, value);
    }

    private static UnsafeBuffer createBuffer(long defaultValue) {
        UnsafeBuffer buffer = new UnsafeBuffer(new byte[LENGTH_WITH_PADDING], VALUE_OFFSET, BitUtil.SIZE_OF_LONG);
        buffer.checkAlignment();
        buffer.putLong(VALUE_OFFSET, defaultValue);
        return buffer;
    }

}
