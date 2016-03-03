package org.efix.util;

public final class LangUtil {

    public static RuntimeException rethrowUnchecked(Exception e) {
        LangUtil.<RuntimeException>rethrow(e);
        throw new AssertionError("Unreachable code");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrow(Exception e) throws T {
        throw (T) e;
    }

}
