package org.f1x.util.concurrent;

import org.f1x.util.LangUtil;
import sun.misc.Contended;

import static org.f1x.util.UnsafeAccess.UNSAFE;

public final class Sequence {

    private static final long VALUE_FIELD_OFFSET;

    static {
        try {
            VALUE_FIELD_OFFSET = UNSAFE.objectFieldOffset(Sequence.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw LangUtil.rethrowUnchecked(e);
        }
    }

    @Contended
    private long value;

    public Sequence() {
    }

    public Sequence(long value) {
        this.value = value;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }

    public void setOrdered(long value) {
        UNSAFE.putOrderedLong(this, VALUE_FIELD_OFFSET, value);
    }

    public long getVolatile() {
        return UNSAFE.getLongVolatile(this, VALUE_FIELD_OFFSET);
    }

    public void setVolitile() {
        UNSAFE.putLongVolatile(this, VALUE_FIELD_OFFSET, value);
    }

    public boolean compareAndSwap(long expected, long updated) {
        return UNSAFE.compareAndSwapLong(this, VALUE_FIELD_OFFSET, expected, updated);
    }

}
