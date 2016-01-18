package org.f1x.util.parse;

public class ParserUtil {

    public static void checkFreeSpace(int free, int required) {
        if (free < required)
            throw new ParserException(String.format("free %s < required %s", free, required));
    }

    public static int checkDigit(byte b) {
        if (!isDigit(b))
            throwInvalidChar(b);

        return digit(b);
    }

    public static byte checkByte(byte b, byte expected) {
        if (b != expected)
            throwInvalidChar(b);

        return b;
    }

    public static byte checkByteNotEqual(byte b, byte notExpected) {
        if (b == notExpected)
            throwInvalidChar(b);

        return b;
    }

    public static int digit(byte b) {
        return b - '0';
    }

    public static boolean isDigit(byte b) {
        return byteInRange(b, '0', '9');
    }

    public static boolean byteInRange(byte b, char from, char to) {
        return b >= from && b <= to;
    }

    public static ParserException throwInvalidChar(byte b) {
        throw new ParserException("invalid character " + (char) b);
    }

    public static ParserException throwSeparatorNotFound(byte separator) {
        throw new ParserException(String.format("separator %s is not found", (char) separator));
    }

}
