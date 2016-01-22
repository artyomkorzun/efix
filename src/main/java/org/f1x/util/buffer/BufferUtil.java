package org.f1x.util.buffer;

import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.StringUtil;

public class BufferUtil {

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
