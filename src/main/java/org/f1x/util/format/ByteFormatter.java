package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.ByteType;

import static org.f1x.util.format.FormatterUtil.checkFreeSpace;

public class ByteFormatter {

    public static void formatByte(byte value, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, ByteType.LENGTH);
        buffer.putByte(off, value);
        offset.set(off + 1);
    }

    public static void formatBytes(byte[] value, int valueOffset, int valueLength, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, valueLength);
        buffer.putBytes(off, value, valueOffset, valueLength);
        offset.set(off + valueLength);
    }

    public static void formatBytes(Buffer value, int valueOffset, int valueLength, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, valueLength);
        buffer.putBytes(off, value, valueOffset, valueLength);
        offset.set(off + valueLength);
    }

}
