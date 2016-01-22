package org.f1x.util.concurrent;

import static org.f1x.util.BitUtil.calculateShift;
import static org.f1x.util.UnsafeAccess.UNSAFE;

/**
 * Doesn't check bounds
 */
public final class UnsafeArray<E> {

    private static final int ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(Object[].class);
    private static final int ARRAY_INDEX_SHIFT = calculateShift(UNSAFE.arrayIndexScale(Object[].class));

    private final E[] array;

    @SuppressWarnings("unchecked")
    public UnsafeArray(int capacity) {
        array = (E[]) new Object[capacity];
    }

    public void setObject(int index, E e) {
        UNSAFE.putObject(array, elementAddress(index), e);
    }

    public void setOrderedObject(int index, E e) {
        UNSAFE.putOrderedObject(array, elementAddress(index), e);
    }

    public void setObjectVolatile(int index, E e) {
        UNSAFE.putObjectVolatile(array, elementAddress(index), e);
    }

    @SuppressWarnings("unchecked")
    public E getObject(int index) {
        return (E) UNSAFE.getObject(array, elementAddress(index));
    }

    @SuppressWarnings("unchecked")
    public E getObjectVolatile(int index) {
        return (E) UNSAFE.getObjectVolatile(array, elementAddress(index));
    }

    public boolean compareAndSetObject(int index, E expected, E updated) {
        return UNSAFE.compareAndSwapObject(array, elementAddress(index), expected, updated);
    }

    private static long elementAddress(int index) {
        return ARRAY_BASE_OFFSET + ((long) index << ARRAY_INDEX_SHIFT);
    }

}
