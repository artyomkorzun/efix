package org.f1x.util.buffer;

import org.f1x.util.Checker;
import org.f1x.util.UnsafeAccess;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

import static org.f1x.util.Bits.*;

public class UnsafeBuffer implements AtomicBuffer {

    public static final int ALIGNMENT = SIZE_OF_LONG;
    public static final int ALIGNMENT_MASK = ALIGNMENT - 1;

    private static final Unsafe UNSAFE = UnsafeAccess.UNSAFE;
    private static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    private byte[] byteArray;
    private ByteBuffer byteBuffer;
    private long addressOffset;

    private int capacity;

    public UnsafeBuffer() {
    }

    public UnsafeBuffer(byte[] buffer) {
        wrap(buffer);
    }

    public UnsafeBuffer(byte[] buffer, int offset, int length) {
        wrap(buffer, offset, length);
    }

    public UnsafeBuffer(ByteBuffer buffer) {
        wrap(buffer);
    }

    public UnsafeBuffer(ByteBuffer buffer, int offset, int length) {
        wrap(buffer, offset, length);
    }

    public UnsafeBuffer(Buffer buffer) {
        wrap(buffer);
    }

    public UnsafeBuffer(Buffer buffer, int offset, int length) {
        wrap(buffer, offset, length);
    }

    public UnsafeBuffer(long address, int length) {
        wrap(address, length);
    }

    public void wrap(byte[] buffer) {
        addressOffset = ARRAY_BASE_OFFSET;
        capacity = buffer.length;
        byteArray = buffer;
        byteBuffer = null;
    }

    public void wrap(byte[] buffer, int offset, int length) {
        Checker.boundsCheck(buffer, offset, length);

        addressOffset = ARRAY_BASE_OFFSET + offset;
        capacity = length;
        byteArray = buffer;
        byteBuffer = null;
    }

    public void wrap(ByteBuffer buffer) {
        byteBuffer = buffer;

        if (buffer.hasArray()) {
            byteArray = buffer.array();
            addressOffset = ARRAY_BASE_OFFSET + buffer.arrayOffset();
        } else {
            byteArray = null;
            addressOffset = ((sun.nio.ch.DirectBuffer) buffer).address();
        }

        capacity = buffer.capacity();
    }

    public void wrap(ByteBuffer buffer, int offset, int length) {
        Checker.boundsCheck(buffer, offset, length);

        byteBuffer = buffer;

        if (buffer.hasArray()) {
            byteArray = buffer.array();
            addressOffset = ARRAY_BASE_OFFSET + buffer.arrayOffset() + offset;
        } else {
            byteArray = null;
            addressOffset = ((sun.nio.ch.DirectBuffer) buffer).address() + offset;
        }

        capacity = length;
    }

    public void wrap(Buffer buffer) {
        addressOffset = buffer.addressOffset();
        capacity = buffer.capacity();
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(Buffer buffer, int offset, int length) {
        Checker.boundsCheck(buffer, offset, length);

        addressOffset = buffer.addressOffset() + offset;
        capacity = length;
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(long address, int length) {
        addressOffset = address;
        capacity = length;
        byteArray = null;
        byteBuffer = null;
    }

    public long addressOffset() {
        return addressOffset;
    }

    public byte[] byteArray() {
        return byteArray;
    }

    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    public void setMemory(int offset, int length, byte value) {
        boundsCheck(offset, length);

        UNSAFE.setMemory(byteArray, addressOffset + offset, length, value);
    }

    public int capacity() {
        return capacity;
    }

    public void verifyAlignment() {
        if ((addressOffset & ALIGNMENT_MASK) != 0) {
            String msg = String.format("AtomicBuffer is not correctly aligned: addressOffset=%d in not divisible by %d", addressOffset, ALIGNMENT);
            throw new IllegalStateException(msg);
        }
    }

    public long getLong(int offset) {
        boundsCheck(offset, SIZE_OF_LONG);

        return UNSAFE.getLong(byteArray, addressOffset + offset);
    }

    public void putLong(int offset, long value) {
        boundsCheck(offset, SIZE_OF_LONG);

        UNSAFE.putLong(byteArray, addressOffset + offset, value);
    }

    public long getLongVolatile(int offset) {
        boundsCheck(offset, SIZE_OF_LONG);

        return UNSAFE.getLongVolatile(byteArray, addressOffset + offset);
    }

    public void putLongVolatile(int offset, long value) {
        boundsCheck(offset, SIZE_OF_LONG);

        UNSAFE.putLongVolatile(byteArray, addressOffset + offset, value);
    }

    public void putLongOrdered(int offset, long value) {
        boundsCheck(offset, SIZE_OF_LONG);

        UNSAFE.putOrderedLong(byteArray, addressOffset + offset, value);
    }

    public boolean compareAndSetLong(int offset, long expectedValue, long updateValue) {
        boundsCheck(offset, SIZE_OF_LONG);

        return UNSAFE.compareAndSwapLong(byteArray, addressOffset + offset, expectedValue, updateValue);
    }

    public long getAndSetLong(int offset, long value) {
        boundsCheck(offset, SIZE_OF_LONG);

        byte[] byteArray = this.byteArray;
        long address = addressOffset + offset;
        long current;

        do {
            current = UNSAFE.getLongVolatile(byteArray, address);
        } while (!UNSAFE.compareAndSwapLong(byteArray, address, current, value));

        return current;
    }

    public long getAndAddLong(int offset, long delta) {
        boundsCheck(offset, SIZE_OF_LONG);

        byte[] byteArray = this.byteArray;
        long address = addressOffset + offset;
        long current;

        do {
            current = UNSAFE.getLongVolatile(byteArray, address);
        } while (!UNSAFE.compareAndSwapLong(byteArray, address, current, current + delta));

        return current;
    }

    public int getInt(int offset) {
        boundsCheck(offset, SIZE_OF_INT);

        return UNSAFE.getInt(byteArray, addressOffset + offset);
    }

    public void putInt(int offset, int value) {
        boundsCheck(offset, SIZE_OF_INT);

        UNSAFE.putInt(byteArray, addressOffset + offset, value);
    }

    public int getIntVolatile(int offset) {
        boundsCheck(offset, SIZE_OF_INT);

        return UNSAFE.getIntVolatile(byteArray, addressOffset + offset);
    }

    public void putIntVolatile(int offset, int value) {
        boundsCheck(offset, SIZE_OF_INT);

        UNSAFE.putIntVolatile(byteArray, addressOffset + offset, value);
    }

    public void putIntOrdered(int offset, int value) {
        boundsCheck(offset, SIZE_OF_INT);

        UNSAFE.putOrderedInt(byteArray, addressOffset + offset, value);
    }


    public boolean compareAndSetInt(int offset, int expectedValue, int updateValue) {
        boundsCheck(offset, SIZE_OF_INT);

        return UNSAFE.compareAndSwapInt(byteArray, addressOffset + offset, expectedValue, updateValue);
    }

    public int getAndSetInt(int offset, int value) {
        boundsCheck(offset, SIZE_OF_INT);

        byte[] byteArray = this.byteArray;
        long address = addressOffset + offset;
        int current;

        do {
            current = UNSAFE.getIntVolatile(byteArray, address);
        } while (!UNSAFE.compareAndSwapInt(byteArray, address, current, value));

        return current;
    }

    public int getAndAddInt(int offset, int delta) {
        boundsCheck(offset, SIZE_OF_INT);

        byte[] byteArray = this.byteArray;
        long address = addressOffset + offset;
        int current;

        do {
            current = UNSAFE.getInt(byteArray, address);
        } while (!UNSAFE.compareAndSwapInt(byteArray, address, current, current + delta));

        return current;
    }

    public double getDouble(int offset) {
        boundsCheck(offset, SIZE_OF_DOUBLE);

        return UNSAFE.getDouble(byteArray, addressOffset + offset);
    }

    public void putDouble(int offset, double value) {
        boundsCheck(offset, SIZE_OF_DOUBLE);

        UNSAFE.putDouble(byteArray, addressOffset + offset, value);
    }

    public float getFloat(int offset) {
        boundsCheck(offset, SIZE_OF_FLOAT);

        return UNSAFE.getFloat(byteArray, addressOffset + offset);
    }

    public void putFloat(int offset, float value) {
        boundsCheck(offset, SIZE_OF_FLOAT);

        UNSAFE.putFloat(byteArray, addressOffset + offset, value);
    }

    public short getShort(int offset) {
        boundsCheck(offset, SIZE_OF_SHORT);

        return UNSAFE.getShort(byteArray, addressOffset + offset);
    }

    public void putShort(int offset, short value) {
        boundsCheck(offset, SIZE_OF_SHORT);

        UNSAFE.putShort(byteArray, addressOffset + offset, value);
    }

    public short getShortVolatile(int offset) {
        boundsCheck(offset, SIZE_OF_SHORT);

        return UNSAFE.getShortVolatile(byteArray, addressOffset + offset);
    }

    public void putShortVolatile(int offset, short value) {
        boundsCheck(offset, SIZE_OF_SHORT);

        UNSAFE.putShortVolatile(byteArray, addressOffset + offset, value);
    }

    public byte getByteVolatile(int offset) {
        boundsCheck(offset, SIZE_OF_BYTE);

        return UNSAFE.getByteVolatile(byteArray, addressOffset + offset);
    }

    public void putByteVolatile(int offset, byte value) {
        boundsCheck(offset, SIZE_OF_BYTE);

        UNSAFE.putByteVolatile(byteArray, addressOffset + offset, value);
    }

    public byte getByte(int offset) {
        boundsCheck(offset, SIZE_OF_BYTE);

        return UNSAFE.getByte(byteArray, addressOffset + offset);
    }

    public void putByte(int offset, byte value) {
        boundsCheck(offset, SIZE_OF_BYTE);

        UNSAFE.putByte(byteArray, addressOffset + offset, value);
    }

    public void getBytes(int offset, byte[] dst) {
        boundsCheck(offset, dst.length);

        UNSAFE.copyMemory(byteArray, addressOffset + offset, dst, ARRAY_BASE_OFFSET, dst.length);
    }

    public void getBytes(int offset, byte[] dst, int dstOffset, int length) {
        boundsCheck(offset, length);
        Checker.boundsCheck(dst, dstOffset, length);

        UNSAFE.copyMemory(byteArray, addressOffset + offset, dst, ARRAY_BASE_OFFSET + dstOffset, length);
    }

    public void getBytes(int offset, MutableBuffer dstBuffer, int dstOffset, int length) {
        dstBuffer.putBytes(dstOffset, this, offset, length);
    }

    public void getBytes(int offset, ByteBuffer dstBuffer, int length) {
        boundsCheck(offset, length);
        int dstOffset = dstBuffer.position();
        Checker.boundsCheck(dstBuffer, dstOffset, length);

        byte[] dstByteArray;
        long dstBaseOffset;
        if (dstBuffer.hasArray()) {
            dstByteArray = dstBuffer.array();
            dstBaseOffset = ARRAY_BASE_OFFSET + dstBuffer.arrayOffset();
        } else {
            dstByteArray = null;
            dstBaseOffset = ((sun.nio.ch.DirectBuffer) dstBuffer).address();
        }

        UNSAFE.copyMemory(byteArray, addressOffset + offset, dstByteArray, dstBaseOffset + dstOffset, length);
        dstBuffer.position(dstBuffer.position() + length);
    }

    public void putBytes(int offset, byte[] src) {
        boundsCheck(offset, src.length);

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET, byteArray, addressOffset + offset, src.length);
    }

    public void putBytes(int offset, byte[] src, int srcOffset, int length) {
        boundsCheck(offset, length);
        Checker.boundsCheck(src, srcOffset, length);

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET + srcOffset, byteArray, addressOffset + offset, length);
    }

    public void putBytes(int offset, ByteBuffer srcBuffer, int length) {
        boundsCheck(offset, length);
        int srcOffset = srcBuffer.position();
        Checker.boundsCheck(srcBuffer, srcOffset, length);

        putBytes(offset, srcBuffer, srcOffset, length);
        srcBuffer.position(srcOffset + length);
    }

    public void putBytes(int offset, ByteBuffer srcBuffer, int srcOffset, int length) {
        boundsCheck(offset, length);
        Checker.boundsCheck(srcBuffer, srcOffset, length);

        byte[] srcByteArray;
        long srcBaseOffset;
        if (srcBuffer.hasArray()) {
            srcByteArray = srcBuffer.array();
            srcBaseOffset = ARRAY_BASE_OFFSET + srcBuffer.arrayOffset();
        } else {
            srcByteArray = null;
            srcBaseOffset = ((sun.nio.ch.DirectBuffer) srcBuffer).address();
        }

        UNSAFE.copyMemory(srcByteArray, srcBaseOffset + srcOffset, byteArray, addressOffset + offset, length);
    }

    @Override
    public void putBytes(int offset, Buffer srcBuffer) {
        int length = srcBuffer.capacity();
        boundsCheck(offset, length);

        UNSAFE.copyMemory(
                srcBuffer.byteArray(),
                srcBuffer.addressOffset(),
                byteArray,
                addressOffset + offset,
                length);
    }

    public void putBytes(int offset, Buffer srcBuffer, int srcOffset, int length) {
        boundsCheck(offset, length);
        srcBuffer.boundsCheck(srcOffset, length);

        UNSAFE.copyMemory(
                srcBuffer.byteArray(),
                srcBuffer.addressOffset() + srcOffset,
                byteArray,
                addressOffset + offset,
                length);
    }

    public void boundsCheck(int offset, int length) {
        Checker.boundsCheck(offset, length, capacity);
    }

}
