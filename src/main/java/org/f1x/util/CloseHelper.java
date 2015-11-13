package org.f1x.util;

public class CloseHelper {

    public static void quietClose(AutoCloseable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Exception ignore) {
        }
    }


    public static void close(AutoCloseable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Exception e) {
            LangUtil.rethrowUnchecked(e);
        }
    }

}
