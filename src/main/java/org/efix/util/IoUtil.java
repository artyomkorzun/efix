package org.efix.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class IoUtil {

    public static void createParentDirectoies(Path file) {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            LangUtil.rethrow(e);
        }
    }

}
