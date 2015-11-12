package org.f1x.util;

public final class LangUtil {

    public static RuntimeException rethrowUnchecked(final Exception ex) {
        LangUtil.<RuntimeException>rethrow(ex);
        throw new AssertionError("Unreachable code");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrow(final Exception ex) throws T {
        throw (T) ex;
    }

}
