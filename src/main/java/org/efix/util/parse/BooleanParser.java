package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.BooleanType;

import static org.efix.util.parse.ParserUtil.*;

public class BooleanParser {

    public static boolean parseBoolean(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(BooleanType.LENGTH + SEPARATOR_LENGTH, end - off);

        boolean value = toBoolean(buffer.getByte(off++));
        checkByte(buffer.getByte(off++), separator);

        offset.set(off);
        return value;
    }

    protected static boolean toBoolean(byte b) {
        switch (b) {
            case BooleanType.TRUE:
                return true;
            case BooleanType.FALSE:
                return false;
            default:
                throw throwUnexpectedByte(b);
        }
    }

}
