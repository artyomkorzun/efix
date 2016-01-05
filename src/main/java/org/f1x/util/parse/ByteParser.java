package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.ParserUtil.*;

public class ByteParser {

    protected static final int BYTE_LENGTH = 1;

    public static byte parseByte(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, ParserUtil.MIN_LENGTH);

        byte b = checkByteNotEqual(buffer.getByte(off), separator);
        checkByte(buffer.getByte(off + 1), separator);

        offset.value(off + BYTE_LENGTH + 1);

        return b;
    }

}
