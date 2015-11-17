package org.f1x.util.parse.newOne;

public class ParserUtil {

    static void ensureSize(int required, int offset, int end) {
        if (required > end - offset)
            throw new ParserException(String.format("No available size %s, offset %s, end %s", required, offset, end));
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
