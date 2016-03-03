package org.efix.util.concurrent;

import org.efix.util.BitUtil;

import static org.efix.util.UnsafeAccess.UNSAFE;

/**
 * Doesn't check bounds
 */
public final class UnsafeArray<E> {

    private static final int ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(Object[].class);
    private static final int ARRAY_INDEX_SHIFT = BitUtil.calculateShift(UNSAFE.arrayIndexScale(Object[].class));

    private final E[] array;

    @SuppressWarnings("unchecked")
    public UnsafeArray(int capacity) {
        array = (E[]) new Object[capacity];
    }

    public void set(int index, E e) {
        UNSAFE.putObject(array, address(index), e);
    }

    public void setOrdered(int index, E e) {
        UNSAFE.putOrderedObject(array, address(index), e);
    }

    public void setVolatile(int index, E e) {
        UNSAFE.putObjectVolatile(array, address(index), e);
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) UNSAFE.getObject(array, address(index));
    }

    @SuppressWarnings("unchecked")
    public E getVolatile(int index) {
        return (E) UNSAFE.getObjectVolatile(array, address(index));
    }

    public boolean compareAndSet(int index, E expected, E updated) {
        return UNSAFE.compareAndSwapObject(array, address(index), expected, updated);
    }

    public int length() {
        return array.length;
    }

    private static long address(int index) {
        return ARRAY_BASE_OFFSET + ((long) index << ARRAY_INDEX_SHIFT);
    }

}
