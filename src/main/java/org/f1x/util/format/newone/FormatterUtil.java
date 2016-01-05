package org.f1x.util.format.newone;

public class FormatterUtil {

    public static void checkFreeSpace(int free, int required) {
        if (free < required)
            throw new FormatterException(String.format("free %s < required %s", free, required));
    }

}
