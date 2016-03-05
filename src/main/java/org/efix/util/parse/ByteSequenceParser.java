package org.efix.util.parse;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.ByteSequenceType;

import static org.efix.util.parse.ParserUtil.*;


public class ByteSequenceParser {

    public static ByteSequence parseByteSequence(byte separator, Buffer buffer, MutableInt offset, int end, ByteSequenceWrapper sequence) {
        int off = offset.get();
        int start = off;
        checkBounds(ByteSequenceType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);
        checkByteNotEqual(buffer.getByte(off++), separator);

        do {
            if (buffer.getByte(off++) == separator) {
                sequence.wrap(buffer, start, off - start - SEPARATOR_LENGTH);
                offset.set(off);
                return sequence;
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    public static void parseByteSequence(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(ByteSequenceType.MIN_LENGTH + SEPARATOR_LENGTH, end - off);
        checkByteNotEqual(buffer.getByte(off++), separator);

        do {
            if (buffer.getByte(off++) == separator) {
                offset.set(off);
                return;
            }
        } while (off < end);

        throwSeparatorNotFound(separator);
    }

}
