package org.efix.util.concurrent;

import org.efix.util.LangUtil;
import org.efix.util.UnsafeAccess;
import sun.misc.Contended;

public final class AtomicLong {

    private static final long VALUE_FIELD_OFFSET;

    static {
        try {
            VALUE_FIELD_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw LangUtil.rethrowUnchecked(e);
        }
    }

    @Contended
    private long value;

    public AtomicLong() {
    }

    public AtomicLong(long value) {
        setVolatile(value);
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }

    public void setOrdered(long value) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, VALUE_FIELD_OFFSET, value);
    }

    public long getVolatile() {
        return UnsafeAccess.UNSAFE.getLongVolatile(this, VALUE_FIELD_OFFSET);
    }

    public void setVolatile(long value) {
        UnsafeAccess.UNSAFE.putLongVolatile(this, VALUE_FIELD_OFFSET, value);
    }

    public boolean compareAndSet(long expected, long updated) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, VALUE_FIELD_OFFSET, expected, updated);
    }

}
