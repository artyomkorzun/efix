package org.f1x.util;

import java.nio.charset.StandardCharsets;

public class StringUtil {

    public static byte[] asciiBytes(String s) {
        return s.getBytes(StandardCharsets.US_ASCII);
    }

}
