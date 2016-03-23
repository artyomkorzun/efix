package org.efix.util;

import java.util.List;


public class CloseHelper {

    public static void quietClose(AutoCloseable resource) {
        try {
            if (resource != null)
                resource.close();
        } catch (Exception ignore) {
        }
    }


    public static void close(AutoCloseable resource) {
        try {
            if (resource != null)
                resource.close();
        } catch (Exception e) {
            throw LangUtil.rethrow(e);
        }
    }

    public static void close(List<? extends AutoCloseable> resources) {
        Exception exception = null;
        for (AutoCloseable resource : resources) {
            try {
                resource.close();
            } catch (Exception e) {
                if (exception == null)
                    exception = e;
                else
                    exception.addSuppressed(e);
            }
        }

        if (exception != null)
            throw LangUtil.rethrow(exception);
    }

}
