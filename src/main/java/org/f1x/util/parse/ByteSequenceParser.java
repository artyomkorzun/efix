package org.f1x.util.parse;

import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.type.ByteSequenceType;

import static org.f1x.util.parse.ParserUtil.*;

public class ByteSequenceParser {

    public static void parseByteSequence(byte separator, Buffer buffer, MutableInt offset, int end, ByteSequenceWrapper sequence) {
        int off = offset.value();
        int start = off;
        checkFreeSpace(end - off, ByteSequenceType.MIN_LENGTH + 1);
        checkByteNotEqual(buffer.getByte(off++), separator);

        do {
            if (buffer.getByte(off++) == separator) {
                sequence.wrap(buffer, start, off - start - 1);
                offset.value(off);
                return;
            }
        } while (off < end);

        throwSeparatorNotFound(separator);
    }

    public static void parseByteSequence(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.value();
        checkFreeSpace(end - off, ByteSequenceType.MIN_LENGTH + 1);
        checkByteNotEqual(buffer.getByte(off++), separator);

        do {
            if (buffer.getByte(off++) == separator) {
                offset.value(off);
                return;
            }
        } while (off < end);

        throwSeparatorNotFound(separator);
    }

}
