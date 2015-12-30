package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.newOne.ParserUtil.*;

public class BooleanParser {

    protected static final int BOOLEAN_LENGTH = 1;

    protected static final byte BOOLEAN_TRUE = 'Y';
    protected static final byte BOOLEAN_FALSE = 'N';

    public static boolean parseBoolean(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkMinLength(end - off, MIN_LENGTH);

        boolean value = toBoolean(buffer.getByte(off));
        checkByte(buffer.getByte(off + 1), separator);

        offset.value(off + BOOLEAN_LENGTH + 1);
        return value;
    }

    protected static boolean toBoolean(byte b) {
        switch (b) {
            case BOOLEAN_TRUE:
                return true;
            case BOOLEAN_FALSE:
                return false;
            default:
                throw throwInvalidChar(b);
        }
    }

}
