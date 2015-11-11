package org.f1x.util.buffer;

import org.f1x.util.UnsafeAccess;

import java.nio.ByteBuffer;

import static org.f1x.util.BitUtil.*;
import static org.f1x.util.MemoryAccess.MEMORY;

public class UnsafeBuffer implements AtomicBuffer {

    public static final int ALIGNMENT = SIZE_OF_LONG;
    public static final int ALIGNMENT_MASK = ALIGNMENT - 1;

    public static final String DISABLE_BOUNDS_CHECK_PROP_KEY = "f1x.disable.bounds.check";
    public static final boolean CHECK_BOUNDS = !Boolean.getBoolean(DISABLE_BOUNDS_CHECK_PROP_KEY);

    private static final long ARRAY_BASE_OFFSET = UnsafeAccess.UNSAFE.arrayBaseOffset(byte[].class);

    private byte[] byteArray;
    private ByteBuffer byteBuffer;
    private long addressOffset;

    private int offset;
    private int capacity;

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
        offset = 0;
        capacity = buffer.length;
        byteArray = buffer;
        byteBuffer = null;
    }

    public void wrap(byte[] buffer, int offset, int length) {
        checkBounds(buffer, offset, length);

        addressOffset = ARRAY_BASE_OFFSET + offset;
        this.offset = offset;
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

        offset = 0;
        capacity = buffer.capacity();
    }

    public void wrap(ByteBuffer buffer, int offset, int length) {
        checkBounds(buffer, offset, length);

        byteBuffer = buffer;

        if (buffer.hasArray()) {
            byteArray = buffer.array();
            addressOffset = ARRAY_BASE_OFFSET + buffer.arrayOffset() + offset;
        } else {
            byteArray = null;
            addressOffset = ((sun.nio.ch.DirectBuffer) buffer).address() + offset;
        }

        this.offset = offset;
        capacity = length;
    }

    public void wrap(Buffer buffer) {
        addressOffset = buffer.addressOffset();
        offset = buffer.offset();
        capacity = buffer.capacity();
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(Buffer buffer, int offset, int length) {
        buffer.checkBounds(offset, length);

        addressOffset = buffer.addressOffset() + offset;
        this.offset = buffer.offset() + offset;
        capacity = length;
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(long address, int length) {
        if (CHECK_BOUNDS) {
            if (address < 0 | length < 0 | address + length < 0)
                throw new IndexOutOfBoundsException(String.format("address=%d, length=%d", address, length));
        }

        addressOffset = address;
        offset = 0;
        capacity = length;
        byteArray = null;
        byteBuffer = null;
    }

    public byte[] byteArray() {
        return byteArray;
    }

    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    public long addressOffset() {
        return addressOffset;
    }

    public int offset() {
        return offset;
    }

    public int capacity() {
        return capacity;
    }

    public void setMemory(int offset, int length, byte value) {
        checkBounds(offset, length);
        MEMORY.setMemory(byteArray, addressOffset + offset, length, value);
    }

    public void checkAlignment() {
        if ((addressOffset & ALIGNMENT_MASK) != 0) {
            throw new IllegalStateException(String.format(
                    "AtomicBuffer is not correctly aligned: addressOffset=%d in not divisible by %d", addressOffset, ALIGNMENT)
            );
        }
    }

    public long getLong(int index) {
        checkBounds(index, SIZE_OF_LONG);
        return MEMORY.getLong(byteArray, addressOffset + index);
    }

    public void putLong(int index, long value) {
        checkBounds(index, SIZE_OF_LONG);
        MEMORY.putLong(byteArray, addressOffset + index, value);
    }

    public long getLongVolatile(int index) {
        checkBounds(index, SIZE_OF_LONG);
        return MEMORY.getLongVolatile(byteArray, addressOffset + index);
    }

    public void putLongVolatile(int index, long value) {
        checkBounds(index, SIZE_OF_LONG);
        MEMORY.putLongVolatile(byteArray, addressOffset + index, value);
    }

    public void putLongOrdered(int index, long value) {
        checkBounds(index, SIZE_OF_LONG);
        MEMORY.putOrderedLong(byteArray, addressOffset + index, value);
    }

    public long addLongOrdered(int index, long increment) {
        checkBounds(index, SIZE_OF_LONG);

        long offset = addressOffset + index;
        byte[] byteArray = this.byteArray;
        long value = MEMORY.getLong(byteArray, offset);
        MEMORY.putOrderedLong(byteArray, offset, value + increment);

        return value;
    }

    public boolean compareAndSetLong(int index, long expectedValue, long updateValue) {
        checkBounds(index, SIZE_OF_LONG);
        return MEMORY.compareAndSwapLong(byteArray, addressOffset + index, expectedValue, updateValue);
    }

    public long getAndSetLong(int index, long value) {
        checkBounds(index, SIZE_OF_LONG);
        return MEMORY.getAndSetLong(byteArray, addressOffset + index, value);
    }

    public long getAndAddLong(final int index, final long delta) {
        checkBounds(index, SIZE_OF_LONG);
        return MEMORY.getAndAddLong(byteArray, addressOffset + index, delta);
    }

    public int getInt(int index) {
        checkBounds(index, SIZE_OF_INT);
        return MEMORY.getInt(byteArray, addressOffset + index);
    }

    public void putInt(int index, int value) {
        checkBounds(index, SIZE_OF_INT);
        MEMORY.putInt(byteArray, addressOffset + index, value);
    }

    public int getIntVolatile(int index) {
        checkBounds(index, SIZE_OF_INT);
        return MEMORY.getIntVolatile(byteArray, addressOffset + index);
    }

    public void putIntVolatile(int index, int value) {
        checkBounds(index, SIZE_OF_INT);
        MEMORY.putIntVolatile(byteArray, addressOffset + index, value);
    }

    public void putIntOrdered(int index, int value) {
        checkBounds(index, SIZE_OF_INT);
        MEMORY.putOrderedInt(byteArray, addressOffset + index, value);
    }

    public int addIntOrdered(int index, int increment) {
        checkBounds(index, SIZE_OF_INT);

        long offset = addressOffset + index;
        byte[] byteArray = this.byteArray;
        int value = MEMORY.getInt(byteArray, offset);
        MEMORY.putOrderedInt(byteArray, offset, value + increment);

        return value;
    }

    public boolean compareAndSetInt(int index, int expectedValue, int updateValue) {
        checkBounds(index, SIZE_OF_INT);
        return MEMORY.compareAndSwapInt(byteArray, addressOffset + index, expectedValue, updateValue);
    }

    public int getAndSetInt(int index, int value) {
        checkBounds(index, SIZE_OF_INT);
        return MEMORY.getAndSetInt(byteArray, addressOffset + index, value);
    }

    public int getAndAddInt(int index, int delta) {
        checkBounds(index, SIZE_OF_INT);
        return MEMORY.getAndAddInt(byteArray, addressOffset + index, delta);
    }

    public double getDouble(int index) {
        checkBounds(index, SIZE_OF_DOUBLE);
        return MEMORY.getDouble(byteArray, addressOffset + index);
    }

    public void putDouble(int index, double value) {
        checkBounds(index, SIZE_OF_DOUBLE);
        MEMORY.putDouble(byteArray, addressOffset + index, value);
    }

    public float getFloat(int index) {
        checkBounds(index, SIZE_OF_FLOAT);
        return MEMORY.getFloat(byteArray, addressOffset + index);
    }

    public void putFloat(int index, float value) {
        checkBounds(index, SIZE_OF_FLOAT);
        MEMORY.putFloat(byteArray, addressOffset + index, value);
    }

    public short getShort(int index) {
        checkBounds(index, SIZE_OF_SHORT);
        return MEMORY.getShort(byteArray, addressOffset + index);
    }

    public void putShort(int index, short value) {
        checkBounds(index, SIZE_OF_SHORT);
        MEMORY.putShort(byteArray, addressOffset + index, value);
    }

    public short getShortVolatile(int index) {
        checkBounds(index, SIZE_OF_SHORT);
        return MEMORY.getShortVolatile(byteArray, addressOffset + index);
    }

    public void putShortVolatile(int index, short value) {
        checkBounds(index, SIZE_OF_SHORT);
        MEMORY.putShortVolatile(byteArray, addressOffset + index, value);
    }

    public char getChar(int index) {
        checkBounds(index, SIZE_OF_CHAR);
        return MEMORY.getChar(byteArray, addressOffset + index);
    }

    public void putChar(int index, char value) {
        checkBounds(index, SIZE_OF_CHAR);
        MEMORY.putChar(byteArray, addressOffset + index, value);
    }

    public char getCharVolatile(int index) {
        checkBounds(index, SIZE_OF_CHAR);
        return MEMORY.getCharVolatile(byteArray, addressOffset + index);
    }

    public void putCharVolatile(int index, char value) {
        checkBounds(index, SIZE_OF_CHAR);
        MEMORY.putCharVolatile(byteArray, addressOffset + index, value);
    }

    public byte getByte(int index) {
        checkBounds(index, SIZE_OF_BYTE);
        return MEMORY.getByte(byteArray, addressOffset + index);
    }

    public void putByte(int index, byte value) {
        checkBounds(index, SIZE_OF_BYTE);
        MEMORY.putByte(byteArray, addressOffset + index, value);
    }

    public byte getByteVolatile(int index) {
        checkBounds(index, SIZE_OF_BYTE);
        return MEMORY.getByteVolatile(byteArray, addressOffset + index);
    }

    public void putByteVolatile(int index, byte value) {
        checkBounds(index, SIZE_OF_BYTE);
        MEMORY.putByteVolatile(byteArray, addressOffset + index, value);
    }

    public void getBytes(int index, byte[] dst) {
        getBytes(index, dst, 0, dst.length);
    }

    public void getBytes(int index, byte[] dst, int offset, int length) {
        checkBounds(index, length);
        checkBounds(dst, offset, length);
        MEMORY.copyMemory(byteArray, addressOffset + index, dst, ARRAY_BASE_OFFSET + offset, length);
    }

    public void getBytes(int index, MutableBuffer dstBuffer, int dstIndex, int length) {
        dstBuffer.putBytes(dstIndex, this, index, length);
    }

    public void getBytes(int index, ByteBuffer dstBuffer, int length) {
        int dstOffset = dstBuffer.position();
        checkBounds(index, length);
        checkBounds(dstBuffer, dstOffset, length);

        byte[] dstByteArray;
        long dstBaseOffset;
        if (dstBuffer.hasArray()) {
            dstByteArray = dstBuffer.array();
            dstBaseOffset = ARRAY_BASE_OFFSET + dstBuffer.arrayOffset();
        } else {
            dstByteArray = null;
            dstBaseOffset = ((sun.nio.ch.DirectBuffer) dstBuffer).address();
        }

        MEMORY.copyMemory(byteArray, addressOffset + index, dstByteArray, dstBaseOffset + dstOffset, length);
        dstBuffer.position(dstBuffer.position() + length);
    }

    public void putBytes(int index, byte[] src) {
        putBytes(index, src, 0, src.length);
    }

    public void putBytes(int index, byte[] src, int offset, int length) {
        checkBounds(index, length);
        checkBounds(src, offset, length);
        MEMORY.copyMemory(src, ARRAY_BASE_OFFSET + offset, byteArray, addressOffset + index, length);
    }

    public void putBytes(int index, ByteBuffer srcBuffer, int length) {
        int srcOffset = srcBuffer.position();
        checkBounds(index, length);
        checkBounds(srcBuffer, srcOffset, length);

        putBytes(index, srcBuffer, srcOffset, length);
        srcBuffer.position(srcOffset + length);
    }

    public void putBytes(int index, ByteBuffer srcBuffer, int srcOffset, int length) {
        checkBounds(index, length);
        checkBounds(srcBuffer, srcOffset, length);

        byte[] srcByteArray;
        long srcBaseOffset;
        if (srcBuffer.hasArray()) {
            srcByteArray = srcBuffer.array();
            srcBaseOffset = ARRAY_BASE_OFFSET + srcBuffer.arrayOffset();
        } else {
            srcByteArray = null;
            srcBaseOffset = ((sun.nio.ch.DirectBuffer) srcBuffer).address();
        }

        MEMORY.copyMemory(srcByteArray, srcBaseOffset + srcOffset, byteArray, addressOffset + index, length);
    }

    @Override
    public void putBytes(int index, Buffer srcBuffer) {
        putBytes(index, srcBuffer, 0, srcBuffer.capacity());
    }

    public void putBytes(int index, Buffer srcBuffer, int srcOffset, int length) {
        checkBounds(index, length);
        srcBuffer.checkBounds(srcOffset, length);

        MEMORY.copyMemory(
                srcBuffer.byteArray(),
                srcBuffer.addressOffset() + srcOffset,
                byteArray,
                addressOffset + index,
                length);
    }

    public void checkBounds(int offset, int length) {
        checkBounds(offset, length, capacity);
    }

    private static void checkBounds(Buffer buffer, int offset, int length) {
        checkBounds(offset, length, buffer.capacity());
    }

    private static void checkBounds(byte[] buffer, int offset, int length) {
        checkBounds(offset, length, buffer.length);
    }

    private static void checkBounds(ByteBuffer buffer, int offset, int length) {
        checkBounds(offset, length, buffer.capacity());
    }

    private static void checkBounds(int offset, int length, int capacity) {
        if (CHECK_BOUNDS) {
            int end = offset + length;
            if (offset < 0 | length < 0 | end < 0 | end > capacity)
                throw new IndexOutOfBoundsException(String.format("offset=%d, length=%d, capacity=%d", offset, length, capacity));
        }
    }

}
