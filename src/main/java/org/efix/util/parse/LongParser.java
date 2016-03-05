package org.efix.util.parse;

import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.LongType;

import static org.efix.util.parse.ParserUtil.*;


public class LongParser {

    protected static final int MAX_ULONG_LENGTH = LongType.MAX_ULONG_LENGTH - 1;

    public static long parseLong(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkFreeSpace(end - off, SIGN_LENGTH);

        if (buffer.getByte(off) == '-') {
            offset.set(off + SIGN_LENGTH);
            return -parseULong(separator, buffer, offset, end);
        } else {
            return parseULong(separator, buffer, offset, end);
        }
    }

    public static long parseULong(byte separator, Buffer buffer, MutableInt offset, int end) {
        int start = offset.get();
        int off = start;

        checkFreeSpace(end - off, LongType.MIN_LENGTH + SEPARATOR_LENGTH);

        byte b = buffer.getByte(off++);
        long value = digit(b);
        if (!isDigit(b))
            throwInvalidChar(b);

        do {
            b = buffer.getByte(off++);
            if (isDigit(b)) {
                value = (value << 3) + (value << 1) + digit(b);
            } else if (b == separator) {
                checkULongLength(off - start - SEPARATOR_LENGTH);
                offset.set(off);
                return value;
            } else {
                throwInvalidChar(b);
            }
        } while (off < end);

        throw throwSeparatorNotFound(separator);
    }

    protected static void checkULongLength(int length) {
        if (length > MAX_ULONG_LENGTH)
            throw new ParserException(String.format("Integer is too long, length %s, max length %s", length, MAX_ULONG_LENGTH));
    }

}
