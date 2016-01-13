package org.f1x.util.format.newone;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.newone.FormatterUtil.checkFreeSpace;

public class ByteFormatter {

    protected static final int BYTE_LENGTH = 1;

    public static void formatByte(byte value, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, BYTE_LENGTH);
        buffer.putByte(off, value);
        offset.value(off + 1);
    }

    public static void formatBytes(byte[] value, int valueOffset, int valueLength, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, valueLength);
        buffer.putBytes(off, value, valueOffset, valueLength);
        offset.value(off + valueLength);
    }

    public static void formatBytes(Buffer value, int valueOffset, int valueLength, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, valueLength);
        buffer.putBytes(off, value, valueOffset, valueLength);
        offset.value(off + valueLength);
    }

}
