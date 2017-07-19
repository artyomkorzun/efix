package org.efix.util.parse;

import org.efix.message.InvalidFieldException;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.type.BooleanType;

import static org.efix.util.parse.ParserUtil.*;


public class BoolParser {

    public static boolean parseBool(int tag, Buffer buffer, int offset, int end) {
        if (offset + 1 != end) {
            throw new InvalidFieldException(tag, "Not valid bool field");
        }

        byte b = buffer.getByte(offset);

        switch (b) {
            case BooleanType.TRUE:
                return true;

            case BooleanType.FALSE:
                return false;

            default:
                throw new InvalidFieldException(tag, "Not bool field. Value: " + (char) b);
        }
    }


    public static boolean parseBool(byte separator, Buffer buffer, MutableInt offset, int end) {
        int off = offset.get();
        checkBounds(BooleanType.LENGTH + SEPARATOR_LENGTH, end - off);

        boolean value = toBool(buffer.getByte(off++));
        checkByte(buffer.getByte(off++), separator);

        offset.set(off);
        return value;
    }

    protected static boolean toBool(byte b) {
        switch (b) {
            case BooleanType.TRUE:
                return true;
            case BooleanType.FALSE:
                return false;
            default:
                throw throwUnexpectedByte(b);
        }
    }

}
