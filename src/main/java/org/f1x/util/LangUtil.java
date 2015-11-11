package org.f1x.util;

public final class LangUtil {

    public static void rethrowUnchecked(final Exception ex) {
        LangUtil.<RuntimeException>rethrow(ex);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrow(final Exception ex) throws T {
        throw (T) ex;
    }

}
