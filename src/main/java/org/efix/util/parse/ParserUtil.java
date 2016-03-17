package org.efix.util.parse;

import org.efix.util.type.ByteType;


public class ParserUtil {

    public static int SIGN_LENGTH = ByteType.LENGTH;
    public static int DOT_LENGTH = ByteType.LENGTH;
    public static int SEPARATOR_LENGTH = ByteType.LENGTH;

    public static void checkBounds(int required, int available) {
        if (available < required)
            throw new ParserException(String.format("Required %s bytes but available %s", required, available));
    }

    public static int checkDigit(byte b) {
        if (!isDigit(b))
            throwUnexpectedByte(b);

        return digit(b);
    }

    public static byte checkByte(byte b, byte expected) {
        if (b != expected)
            throwUnexpectedByte(b);

        return b;
    }

    public static byte checkByteNotEqual(byte b, byte notExpected) {
        if (b == notExpected)
            throwUnexpectedByte(b);

        return b;
    }

    public static int digit(byte b) {
        return b - '0';
    }

    public static boolean isDigit(byte b) {
        return ((b - '0') & 0x7FFFFFFF) <= ('9' - '0');
    }

    public static ParserException throwUnexpectedByte(byte b) {
        throw new ParserException("Unexpected byte " + (char) b);
    }

    public static ParserException throwSeparatorNotFound(byte separator) {
        throw new ParserException(String.format("Separator %s is not found", (char) separator));
    }

}
