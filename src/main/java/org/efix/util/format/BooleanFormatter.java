package org.efix.util.format;

import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.BooleanType;


public class BooleanFormatter {

    public static int formatBoolean(boolean value, MutableBuffer buffer, int offset) {
        byte b = value ? BooleanType.TRUE : BooleanType.FALSE;
        buffer.putByte(offset++, b);
        return offset;
    }

}
