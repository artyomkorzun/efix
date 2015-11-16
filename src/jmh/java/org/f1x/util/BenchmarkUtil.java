package org.f1x.util;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;

public class BenchmarkUtil {

    public static Buffer makeMessage(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

}
