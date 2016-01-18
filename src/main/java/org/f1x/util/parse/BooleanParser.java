package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.type.BooleanType;

import static org.f1x.util.parse.ParserUtil.*;

public class BooleanParser {

    public static boolean parseBoolean(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, BooleanType.LENGTH + 1);

        boolean value = toBoolean(buffer.getByte(off++));
        checkByte(buffer.getByte(off++), separator);

        offset.value(off);
        return value;
    }

    protected static boolean toBoolean(byte b) {
        switch (b) {
            case BooleanType.TRUE:
                return true;
            case BooleanType.FALSE:
                return false;
            default:
                throw throwInvalidChar(b);
        }
    }

}
