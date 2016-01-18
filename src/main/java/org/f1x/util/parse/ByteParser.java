package org.f1x.util.parse;

import org.f1x.message.fields.type.ByteType;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.ParserUtil.*;

public class ByteParser {

    public static byte parseByte(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, ByteType.LENGTH + 1);

        byte b = checkByteNotEqual(buffer.getByte(off++), separator);
        checkByte(buffer.getByte(off++), separator);

        offset.value(off);

        return b;
    }

}
