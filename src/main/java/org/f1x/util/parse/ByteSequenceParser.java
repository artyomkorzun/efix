package org.f1x.util.parse;

import org.f1x.util.ByteSequence;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.parse.ParserUtil.*;

public class ByteSequenceParser {

    public static void parseByteSequence(byte separator, Buffer buffer, MutableInt offset, int end, ByteSequence sequence) {
        int off = offset.value();
        int start = off;
        checkFreeSpace(end - off, ParserUtil.MIN_LENGTH);
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
        checkFreeSpace(end - off, ParserUtil.MIN_LENGTH);
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
