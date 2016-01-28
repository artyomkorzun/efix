package org.f1x.util.format;

import org.f1x.util.buffer.MutableBuffer;


public class CharFormatter {

    public static int formatChar(char value, MutableBuffer buffer, int offset) {
        buffer.putByte(offset++, (byte) value);
        return offset;
    }

    public static int formatChars(CharSequence value, int valueOffset, int valueLength, MutableBuffer buffer, int offset) {
        for (int end = offset + valueLength; offset < end; )
            buffer.putByte(offset++, (byte) value.charAt(valueOffset++));

        return offset;
    }

}
