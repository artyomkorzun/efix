package org.efix.util.parse;

public class ParserUtil {

    public static int SIGN_LENGTH = 1;
    public static int DOT_LENGTH = 1;
    public static int SEPARATOR_LENGTH = 1;

    public static void checkBounds(int available, int required) {
        if (available < required)
            throw new ParserException(String.format("Available %s but required %s", available, required));
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
        throw new ParserException("Invalid character " + (char) b);
    }

    public static ParserException throwSeparatorNotFound(byte separator) {
        throw new ParserException(String.format("Separator %s is not found", (char) separator));
    }

}
