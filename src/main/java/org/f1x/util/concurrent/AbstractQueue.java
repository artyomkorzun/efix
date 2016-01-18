package org.f1x.util.concurrent;

import org.f1x.util.BitUtil;

public abstract class AbstractQueue<E> implements Queue<E> {

    protected final AtomicLong headSequence = new AtomicLong();
    protected final AtomicLong tailSequence = new AtomicLong();
    protected final AtomicLong tailCacheSequence = new AtomicLong();
    protected final UnsafeArray<E> array;
    protected final int mask;
    protected final int capacity;

    public AbstractQueue(int requestedCapacity) {
        capacity = BitUtil.findNextPowerOfTwo(requestedCapacity);
        mask = capacity - 1;
        array = new UnsafeArray<>(capacity);
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        long head = headSequence.getVolatile();
        long tail = tailSequence.getVolatile();
        return (int) (head - tail);
    }

    protected int mask(long sequence) {
        return (int) sequence & mask;
    }

}
