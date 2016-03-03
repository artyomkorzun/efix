package org.efix.util.format;

public class FormatterUtil {

    public static byte digit(int digit) {
        return (byte) (digit + '0');
    }

    // TODO move to MessageFormatter
    public static void checkFreeSpace(int free, int required) {
        if (free < required)
            throw new FormatterException(String.format("free %s < required %s", free, required));
    }

}
