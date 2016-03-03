package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.CharType;

import static org.efix.util.parse.ParserUtil.*;

public class CharParser {

    public static char parseChar(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, CharType.LENGTH + 1);

        byte b = checkByteNotEqual(buffer.getByte(off++), separator);
        checkByte(buffer.getByte(off++), separator);

        offset.set(off);

        return (char) b;
    }

}
