package org.efix.util;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.UnsafeBuffer;

public class BenchmarkUtil {

    public static Buffer makeMessage(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

}
