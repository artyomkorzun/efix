package org.efix.util.buffer;

import org.efix.util.ByteSequenceWrapper;
import org.efix.util.LangUtil;
import org.efix.util.StringUtil;

import java.nio.ByteBuffer;

import static org.efix.util.UnsafeAccess.UNSAFE;

public class BufferUtil {

    public static final long ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
    public static final long BYTE_BUFFER_HB_FIELD_OFFSET;
    public static final long BYTE_BUFFER_OFFSET_FIELD_OFFSET;

    static {
        try {
            BYTE_BUFFER_HB_FIELD_OFFSET = UNSAFE.objectFieldOffset(
                    ByteBuffer.class.getDeclaredField("hb")
            );

            BYTE_BUFFER_OFFSET_FIELD_OFFSET = UNSAFE.objectFieldOffset(
                    ByteBuffer.class.getDeclaredField("offset")
            );
        } catch (final Exception ex) {
            throw LangUtil.rethrow(ex);
        }
    }

    public static UnsafeBuffer fromString(String string) {
        return new UnsafeBuffer(StringUtil.asciiBytes(string));
    }

    public static String toString(Buffer buffer) {
        return toString(buffer, 0, buffer.capacity());
    }

    public static String toString(Buffer buffer, int offset, int length) {
        return new ByteSequenceWrapper(buffer, offset, length).toString();
    }

    /**
     * Get the address at which the underlying buffer storage begins.
     *
     * @param buffer that wraps the underlying storage.
     * @return the memory address at which the buffer storage begins.
     */
    public static long address(final ByteBuffer buffer) {
        return ((sun.nio.ch.DirectBuffer) buffer).address();
    }

    /**
     * Get the array from a read-only {@link ByteBuffer} similar to {@link ByteBuffer#array()}.
     *
     * @param buffer that wraps the underlying array.
     * @return the underlying array.
     */
    public static byte[] array(final ByteBuffer buffer) {
        return (byte[]) UNSAFE.getObject(buffer, BYTE_BUFFER_HB_FIELD_OFFSET);
    }

    /**
     * Get the array offset from a read-only {@link ByteBuffer} similar to {@link ByteBuffer#arrayOffset()}.
     *
     * @param buffer that wraps the underlying array.
     * @return the underlying array offset at which this ByteBuffer starts.
     */
    public static int arrayOffset(final ByteBuffer buffer) {
        return UNSAFE.getInt(buffer, BYTE_BUFFER_OFFSET_FIELD_OFFSET);
    }

    /**
     * Bounds check the access range and throw a {@link IndexOutOfBoundsException} if exceeded.
     *
     * @param buffer to be checked.
     * @param index  at which the access will begin.
     * @param length of the range accessed.
     */
    public static void boundsCheck(final byte[] buffer, final long index, final int length) {
        final int capacity = buffer.length;
        final long resultingPosition = index + (long) length;

        if (index < 0 | resultingPosition > capacity) {
            throw new IndexOutOfBoundsException(String.format(
                    "index=%d, length=%d, capacity=%d",
                    index, length, capacity)
            );
        }
    }

    /**
     * Bounds check the access range and throw a {@link IndexOutOfBoundsException} if exceeded.
     *
     * @param buffer to be checked.
     * @param index  at which the access will begin.
     * @param length of the range accessed.
     */
    public static void boundsCheck(final ByteBuffer buffer, final long index, final int length) {
        final int capacity = buffer.capacity();
        final long resultingPosition = index + (long) length;

        if (index < 0 | resultingPosition > capacity) {
            throw new IndexOutOfBoundsException(String.format(
                    "index=%d, length=%d, capacity=%d",
                    index, length, capacity)
            );
        }
    }

}
