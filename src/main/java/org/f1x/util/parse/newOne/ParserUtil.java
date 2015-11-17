package org.f1x.util.parse.newOne;

public class ParserUtil {

    public static final int MIN_LENGTH = 2;

    public static void checkMinLength(int length) {
        checkMinLength(length, MIN_LENGTH);
    }

    public static void checkMinLength(int length, int min) {
        if (length < min)
            throw new ParserException(String.format("length %s < min %s", length, min));
    }

    public static boolean isDigit(byte b) {
        return b >= '0' & b <= '9';
    }

    public static int digit(byte b) {
        return b - '0';
    }

    public static ParserException throwInvalidChar(byte b) {
        throw new ParserException("Invalid character: " + (char) b);
    }

    public static ParserException throwSeparatorNotFound(byte separator) {
        throw new ParserException(String.format("Separator %s is not found", (char) separator));
    }

}
