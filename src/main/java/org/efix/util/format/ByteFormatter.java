package org.efix.util.format;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;


public class ByteFormatter {

    public static int formatByte(byte value, MutableBuffer buffer, int offset) {
        buffer.putByte(offset++, value);
        return offset;
    }

    public static int formatBytes(byte[] value, int valueOffset, int valueLength, MutableBuffer buffer, int offset) {
        buffer.putBytes(offset, value, valueOffset, valueLength);
        return offset + valueLength;
    }

    public static int formatBytes(Buffer value, int valueOffset, int valueLength, MutableBuffer buffer, int offset) {
        buffer.putBytes(offset, value, valueOffset, valueLength);
        return offset + valueLength;
    }

}
