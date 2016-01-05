package org.f1x.util.format.newone;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

import static org.f1x.util.format.newone.FormatterUtil.checkFreeSpace;

public class CharSequenceFormatter {

    public static void formatCharSequence(CharSequence value, int valueOffset, int valueLength, MutableBuffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, valueLength);

        for (int i = 0; i < valueLength; i++)
            buffer.putByte(off + i, (byte) value.charAt(valueOffset + i));

        offset.value(off + valueLength);
    }

}
