package org.efix.util;

public final class LangUtil {

    public static RuntimeException rethrow(Exception e) {
        LangUtil.<RuntimeException>rethrowUnchecked(e);
        throw new AssertionError("Unreachable code");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrowUnchecked(Exception e) throws T {
        throw (T) e;
    }

}
