package org.f1x.util.concurrent;

import org.f1x.util.BitUtil;

public abstract class AbstractPipe<E> implements Pipe<E> {

    protected final Sequence headSequence = new Sequence();
    protected final Sequence tailSequence = new Sequence();
    protected final Sequence tailCacheSequence = new Sequence();
    protected final UnsafeArray<E> array;
    protected final int mask;
    protected final int capacity;

    public AbstractPipe(int requestedCapacity) {
        capacity = BitUtil.findNextPositivePowerOfTwo(requestedCapacity);
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
