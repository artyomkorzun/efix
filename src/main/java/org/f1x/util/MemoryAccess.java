package org.f1x.util;

import static org.f1x.util.UnsafeAccess.UNSAFE;

public class MemoryAccess {

    public static final MemoryAccess MEMORY = new MemoryAccess();

    public void loadFence() {
        UNSAFE.loadFence();
    }

    public void storeFence() {
        UNSAFE.storeFence();
    }

    public void fullFence() {
        UNSAFE.fullFence();
    }

    public long getLong(Object ref, long offset) {
        return UNSAFE.getLong(ref, offset);
    }

    public void putLong(Object ref, long offset, long value) {
        UNSAFE.putLong(ref, offset, value);
    }

    public long getLongVolatile(Object ref, long offset) {
        return UNSAFE.getLongVolatile(ref, offset);
    }

    public void putLongVolatile(Object ref, long offset, long value) {
        UNSAFE.putLongVolatile(ref, offset, value);
    }

    public void putOrderedLong(Object ref, long offset, long value) {
        UNSAFE.putOrderedLong(ref, offset, value);
    }

    public boolean compareAndSwapLong(
            Object ref,
            long offset,
            long expectedValue,
            long updateValue) {
        return UNSAFE.compareAndSwapLong(ref, offset, expectedValue, updateValue);
    }

    public long getAndSetLong(Object ref, long offset, long value) {
        return UNSAFE.getAndSetLong(ref, offset, value);
    }

    public long getAndAddLong(Object ref, long offset, long delta) {
        return UNSAFE.getAndAddLong(ref, offset, delta);
    }

    public int getInt(Object ref, long offset) {
        return UNSAFE.getInt(ref, offset);
    }

    public void putInt(Object ref, long offset, int value) {
        UNSAFE.putInt(ref, offset, value);
    }

    public int getIntVolatile(Object ref, long offset) {
        return UNSAFE.getIntVolatile(ref, offset);
    }

    public void putIntVolatile(Object ref, long offset, int value) {
        UNSAFE.putIntVolatile(ref, offset, value);
    }

    public void putOrderedInt(Object ref, long offset, int value) {
        UNSAFE.putOrderedInt(ref, offset, value);
    }

    public boolean compareAndSwapInt(Object ref, long offset, int expectedValue, int updateValue) {
        return UNSAFE.compareAndSwapInt(ref, offset, expectedValue, updateValue);
    }

    public int getAndSetInt(Object ref, long offset, int value) {
        return UNSAFE.getAndSetInt(ref, offset, value);
    }

    public int getAndAddInt(Object ref, long offset, int delta) {
        return UNSAFE.getAndAddInt(ref, offset, delta);
    }

    public double getDouble(Object ref, long offset) {
        return UNSAFE.getDouble(ref, offset);
    }

    public void putDouble(Object ref, long offset, double value) {
        UNSAFE.putDouble(ref, offset, value);
    }

    public float getFloat(Object ref, long offset) {
        return UNSAFE.getFloat(ref, offset);
    }

    public void putFloat(Object ref, long offset, float value) {
        UNSAFE.putFloat(ref, offset, value);
    }

    public short getShort(Object ref, long offset) {
        return UNSAFE.getShort(ref, offset);
    }

    public void putShort(Object ref, long offset, short value) {
        UNSAFE.putShort(ref, offset, value);
    }

    public short getShortVolatile(Object ref, long offset) {
        return UNSAFE.getShortVolatile(ref, offset);
    }

    public void putShortVolatile(Object ref, long offset, short value) {
        UNSAFE.putShortVolatile(ref, offset, value);
    }

    public byte getByte(Object ref, long offset) {
        return UNSAFE.getByte(ref, offset);
    }

    public void putByte(Object ref, long offset, byte value) {
        UNSAFE.putByte(ref, offset, value);
    }

    public byte getByteVolatile(Object ref, long offset) {
        return UNSAFE.getByteVolatile(ref, offset);
    }

    public void putByteVolatile(Object ref, long offset, byte value) {
        UNSAFE.putByteVolatile(ref, offset, value);
    }

    public char getChar(Object ref, long offset) {
        return UNSAFE.getChar(ref, offset);
    }

    public void putChar(Object ref, long offset, char value) {
        UNSAFE.putChar(ref, offset, value);
    }

    public char getCharVolatile(Object ref, long offset) {
        return UNSAFE.getCharVolatile(ref, offset);
    }

    public void putCharVolatile(Object ref, long offset, char value) {
        UNSAFE.putCharVolatile(ref, offset, value);
    }

    public void setMemory(Object ref, long offset, int amount, byte value) {
        UNSAFE.setMemory(ref, offset, amount, value);
    }

    public void copyMemory(
            Object from,
            long fromOffset,
            Object to,
            long toOffset,
            int amount) {
        UNSAFE.copyMemory(from, fromOffset, to, toOffset, amount);
    }

}
