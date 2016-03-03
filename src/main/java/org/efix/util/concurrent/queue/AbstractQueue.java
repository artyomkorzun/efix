package org.efix.util.concurrent.queue;

import org.efix.util.BitUtil;
import org.efix.util.concurrent.AtomicLong;
import org.efix.util.concurrent.UnsafeArray;

public abstract class AbstractQueue<E> implements Queue<E> {

    protected final AtomicLong headSequence = new AtomicLong();
    protected final AtomicLong tailSequence = new AtomicLong();
    protected final AtomicLong tailSequenceCache = new AtomicLong();
    protected final UnsafeArray<E> array;
    protected final int mask;
    protected final int capacity;

    public AbstractQueue(int requestedCapacity) {
        capacity = BitUtil.nextPowerOfTwo(requestedCapacity);
        mask = capacity - 1;
        array = new UnsafeArray<>(capacity);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public E poll() {
        long tail = tailSequence.get();
        int index = mask(tail);
        E e = array.getVolatile(index);
        if (e != null) {
            array.set(index, null);
            tailSequence.setOrdered(tail + 1);
        }

        return e;
    }

    @Override
    public int size() {
        long head = headSequence.getVolatile();
        long tail = tailSequence.getVolatile();
        return (int) (head - tail);
    }

    @Override
    public boolean isEmpty() {
        long head = headSequence.getVolatile();
        long tail = tailSequence.getVolatile();
        return tail >= head;
    }

    protected int mask(long sequence) {
        return (int) sequence & mask;
    }

}
