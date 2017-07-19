package org.efix.util.parse;

import org.efix.message.InvalidFieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.ByteType;

import static org.efix.util.parse.ParserUtil.*;

public class ByteParser {

    public static byte parseByte(int tag, Buffer buffer, int offset, int end) {
        if (offset + 1 != end) {
            throw new InvalidFieldException(tag, "Not valid byte field");
        }

        return buffer.getByte(offset);
    }

    public static byte parseByte(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(ByteType.LENGTH + SEPARATOR_LENGTH, end - off);

        byte b = checkByteNotEqual(buffer.getByte(off++), separator);
        checkByte(buffer.getByte(off++), separator);

        offset.set(off);

        return b;
    }

}
