package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.type.CharType;

import static org.f1x.util.parse.ParserUtil.*;

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
