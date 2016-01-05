package org.f1x.util;

import org.f1x.util.buffer.UnsafeBuffer;

public class BufferUtil {

    public static UnsafeBuffer create(String string) {
        return new UnsafeBuffer(StringUtil.asciiBytes(string));
    }

}
