package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.BooleanType;

import static org.f1x.util.format.ByteFormatter.formatByte;

public class BooleanFormatter {

    public static void formatBoolean(boolean value, MutableBuffer buffer, MutableInt offset, int end) {
        formatByte(value ? BooleanType.TRUE : BooleanType.FALSE, buffer, offset, end);
    }

}
