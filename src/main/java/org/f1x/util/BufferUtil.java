package org.f1x.util;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;

import java.nio.ByteBuffer;

public class BufferUtil {

    public static UnsafeBuffer allocate(int capacity) {
        return new UnsafeBuffer(ByteBuffer.allocate(capacity));
    }

    public static UnsafeBuffer allocateDirect(int capacity) {
        return new UnsafeBuffer(ByteBuffer.allocateDirect(capacity));
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

}
