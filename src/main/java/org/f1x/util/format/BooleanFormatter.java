package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;

public class BooleanFormatter {

    protected static final byte BOOLEAN_TRUE = 'Y';
    protected static final byte BOOLEAN_FALSE = 'N';

    public static void formatBoolean(boolean value, MutableBuffer buffer, MutableInt offset, int end) {
        ByteFormatter.formatByte(value ? BOOLEAN_TRUE : BOOLEAN_FALSE, buffer, offset, end);
    }

}
