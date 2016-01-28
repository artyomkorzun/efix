package org.f1x.util.format;

import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.BooleanType;


public class BooleanFormatter {

    public static int formatBoolean(boolean value, MutableBuffer buffer, int offset) {
        byte b = value ? BooleanType.TRUE : BooleanType.FALSE;
        buffer.putByte(offset++, b);
        return offset;
    }

}
