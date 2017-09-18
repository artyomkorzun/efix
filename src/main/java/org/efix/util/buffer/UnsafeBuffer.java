package org.efix.util.buffer;

import org.efix.util.UnsafeAccess;

import java.nio.ByteBuffer;

import static org.efix.util.BitUtil.*;
import static org.efix.util.UnsafeAccess.UNSAFE;
import static org.efix.util.buffer.BufferUtil.*;


public final class UnsafeBuffer implements MutableBuffer {

    public static final String DISABLE_BOUNDS_CHECK_PROP_KEY = "efix.disable.bounds.check";
    public static final boolean SHOULD_BOUNDS_CHECK = !Boolean.getBoolean(DISABLE_BOUNDS_CHECK_PROP_KEY);

    private static final long ARRAY_BASE_OFFSET = UnsafeAccess.UNSAFE.arrayBaseOffset(byte[].class);

    private byte[] byteArray;
    private ByteBuffer byteBuffer;
    private long addressOffset;
    private int capacity;

    public UnsafeBuffer() {
    }

    /**
     * Attach a view to a byte[] for providing direct access.
     *
     * @param buffer to which the view is attached.
     */
    public UnsafeBuffer(final byte[] buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to a byte[] for providing direct access.
     *
     * @param buffer to which the view is attached.
     * @param offset within the buffer to begin.
     * @param length of the buffer to be included.
     */
    public UnsafeBuffer(final byte[] buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to a {@link ByteBuffer} for providing direct access, the {@link ByteBuffer} can be
     * heap based or direct.
     *
     * @param buffer to which the view is attached.
     */
    public UnsafeBuffer(final ByteBuffer buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to a {@link ByteBuffer} for providing direct access, the {@link ByteBuffer} can be
     * heap based or direct.
     *
     * @param buffer to which the view is attached.
     * @param offset within the buffer to begin.
     * @param length of the buffer to be included.
     */
    public UnsafeBuffer(final ByteBuffer buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to an existing {@link Buffer}
     *
     * @param buffer to which the view is attached.
     */
    public UnsafeBuffer(final Buffer buffer) {
        wrap(buffer);
    }

    /**
     * Attach a view to an existing {@link Buffer}
     *
     * @param buffer to which the view is attached.
     * @param offset within the buffer to begin.
     * @param length of the buffer to be included.
     */
    public UnsafeBuffer(final Buffer buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Attach a view to an off-heap memory region by address. This is useful for interacting with native libraries.
     *
     * @param address where the memory begins off-heap
     * @param length  of the buffer from the given address
     */
    public UnsafeBuffer(final long address, final int length) {
        wrap(address, length);
    }

    public void wrap(final byte[] buffer) {
        addressOffset = ARRAY_BASE_OFFSET;
        capacity = buffer.length;
        byteArray = buffer;
        byteBuffer = null;
    }

    public void wrap(final byte[] buffer, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            final int bufferLength = buffer.length;
            if (offset != 0 && (offset < 0 || offset > bufferLength)) {
                throw new IllegalArgumentException("offset=" + offset + " not valid for buffer.length=" + bufferLength);
            }

            if (length < 0 || length > bufferLength - offset) {
                throw new IllegalArgumentException(
                        "offset=" + offset + " length=" + length + " not valid for buffer.length=" + bufferLength);
            }
        }

        addressOffset = ARRAY_BASE_OFFSET + offset;
        capacity = length;
        byteArray = buffer;
        byteBuffer = null;
    }

    public void wrap(final ByteBuffer buffer) {
        byteBuffer = buffer;

        if (buffer.isDirect()) {
            byteArray = null;
            addressOffset = address(buffer);
        } else {
            byteArray = array(byteBuffer);
            addressOffset = ARRAY_BASE_OFFSET + arrayOffset(byteBuffer);
        }

        capacity = buffer.capacity();
    }

    public void wrap(final ByteBuffer buffer, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            final int bufferCapacity = buffer.capacity();
            if (offset != 0 && (offset < 0 || offset > bufferCapacity)) {
                throw new IllegalArgumentException("offset=" + offset + " not valid for capacity=" + bufferCapacity);
            }

            if (length < 0 || length > bufferCapacity - offset) {
                throw new IllegalArgumentException(
                        "offset=" + offset + " length=" + length + " not valid for capacity=" + bufferCapacity);
            }
        }

        byteBuffer = buffer;

        if (buffer.isDirect()) {
            byteArray = null;
            addressOffset = address(buffer) + offset;
        } else {
            byteArray = array(buffer);
            addressOffset = ARRAY_BASE_OFFSET + arrayOffset(buffer) + offset;
        }

        capacity = length;
    }

    public void wrap(final Buffer buffer) {
        addressOffset = buffer.addressOffset();
        capacity = buffer.capacity();
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(final Buffer buffer, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            final int bufferCapacity = buffer.capacity();
            if (offset != 0 && (offset < 0 || offset > bufferCapacity)) {
                throw new IllegalArgumentException("offset=" + offset + " not valid for capacity=" + bufferCapacity);
            }

            if (length < 0 || length > bufferCapacity - offset) {
                throw new IllegalArgumentException(
                        "offset=" + offset + " length=" + length + " not valid for capacity=" + bufferCapacity);
            }
        }

        addressOffset = buffer.addressOffset() + offset;
        capacity = length;
        byteArray = buffer.byteArray();
        byteBuffer = buffer.byteBuffer();
    }

    public void wrap(final long address, final int length) {
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

    public void setMemory(final int index, final int length, final byte value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        final long indexOffset = addressOffset + index;
        if (0 == (indexOffset & 1) && length > 64) {
            // This horrible filth is to encourage the JVM to call memset() when address is even.
            // TODO: check if this still applies when Java 9 is out!!!
            UNSAFE.putByte(byteArray, indexOffset, value);
            UNSAFE.setMemory(byteArray, indexOffset + 1, length - 1, value);
        } else {
            UNSAFE.setMemory(byteArray, indexOffset, length, value);
        }
    }

    public int capacity() {
        return capacity;
    }

    public long getLong(final int index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        return UNSAFE.getLong(byteArray, addressOffset + index);
    }

    public void putLong(final int index, final long value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_LONG);
        }

        UNSAFE.putLong(byteArray, addressOffset + index, value);
    }

    public int getInt(final int index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        return UNSAFE.getInt(byteArray, addressOffset + index);
    }

    public void putInt(final int index, final int value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_INT);
        }

        UNSAFE.putInt(byteArray, addressOffset + index, value);
    }

    public short getShort(final int index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        return UNSAFE.getShort(byteArray, addressOffset + index);
    }

    public void putShort(final int index, final short value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, SIZE_OF_SHORT);
        }

        UNSAFE.putShort(byteArray, addressOffset + index, value);
    }

    public byte getByte(final int index) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        return UNSAFE.getByte(byteArray, addressOffset + index);
    }

    public void putByte(final int index, final byte value) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck(index);
        }

        UNSAFE.putByte(byteArray, addressOffset + index, value);
    }

    public void getBytes(final int index, final byte[] dst) {
        getBytes(index, dst, 0, dst.length);
    }

    public void getBytes(final int index, final byte[] dst, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(dst, offset, length);
        }

        UNSAFE.copyMemory(byteArray, addressOffset + index, dst, ARRAY_BASE_OFFSET + offset, length);
    }

    public void getBytes(final int index, final MutableBuffer dstBuffer, final int dstIndex, final int length) {
        dstBuffer.putBytes(dstIndex, this, index, length);
    }

    public void getBytes(final int index, final ByteBuffer dstBuffer, final int length) {
        final int dstOffset = dstBuffer.position();
        getBytes(index, dstBuffer, dstOffset, length);
        dstBuffer.position(dstOffset + length);
    }

    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstOffset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(dstBuffer, (long) dstOffset, length);
        }

        final byte[] dstByteArray;
        final long dstBaseOffset;
        if (dstBuffer.isDirect()) {
            dstByteArray = null;
            dstBaseOffset = address(dstBuffer);
        } else {
            dstByteArray = array(dstBuffer);
            dstBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(dstBuffer);
        }

        UNSAFE.copyMemory(byteArray, addressOffset + index, dstByteArray, dstBaseOffset + dstOffset, length);
    }

    public void putBytes(final int index, final byte[] src) {
        putBytes(index, src, 0, src.length);
    }

    public void putBytes(final int index, final byte[] src, final int offset, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(src, offset, length);
        }

        UNSAFE.copyMemory(src, ARRAY_BASE_OFFSET + offset, byteArray, addressOffset + index, length);
    }

    public void putBytes(final int index, final ByteBuffer srcBuffer, final int length) {
        final int srcIndex = srcBuffer.position();
        putBytes(index, srcBuffer, srcIndex, length);
        srcBuffer.position(srcIndex + length);
    }

    public void putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            BufferUtil.boundsCheck(srcBuffer, srcIndex, length);
        }

        final byte[] srcByteArray;
        final long srcBaseOffset;
        if (srcBuffer.isDirect()) {
            srcByteArray = null;
            srcBaseOffset = address(srcBuffer);
        } else {
            srcByteArray = array(srcBuffer);
            srcBaseOffset = ARRAY_BASE_OFFSET + arrayOffset(srcBuffer);
        }

        UNSAFE.copyMemory(srcByteArray, srcBaseOffset + srcIndex, byteArray, addressOffset + index, length);
    }

    public void putBytes(final int index, final Buffer srcBuffer) {
        final int length = srcBuffer.capacity();

        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
        }

        UNSAFE.copyMemory(
                srcBuffer.byteArray(),
                srcBuffer.addressOffset(),
                byteArray,
                addressOffset + index,
                length);
    }

    public void putBytes(final int index, final Buffer srcBuffer, final int srcIndex, final int length) {
        if (SHOULD_BOUNDS_CHECK) {
            boundsCheck0(index, length);
            srcBuffer.boundsCheck(srcIndex, length);
        }

        UNSAFE.copyMemory(
                srcBuffer.byteArray(),
                srcBuffer.addressOffset() + srcIndex,
                byteArray,
                addressOffset + index,
                length);
    }

    public void boundsCheck(final int index, final int length) {
        boundsCheck0(index, length);
    }

    private void boundsCheck(final int index) {
        if (index < 0 | index >= capacity) {
            throw new IndexOutOfBoundsException(String.format("index=%d, capacity=%d", index, capacity));
        }
    }

    private void boundsCheck0(final int index, final int length) {
        final long resultingPosition = index + (long) length;
        if (index < 0 | resultingPosition > capacity) {
            throw new IndexOutOfBoundsException(
                    String.format("index=%d, length=%d, capacity=%d", index, length, capacity));
        }
    }

    public static UnsafeBuffer allocateHeap(int capacity) {
        return new UnsafeBuffer(ByteBuffer.allocate(capacity));
    }

    public static UnsafeBuffer allocateDirect(int capacity) {
        return new UnsafeBuffer(ByteBuffer.allocateDirect(capacity));
    }

}
