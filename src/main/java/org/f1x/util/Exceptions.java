package org.f1x.util;

public final class Exceptions {

    private Exceptions() {
        throw new AssertionError("Not for you, man!");
    }

    public static void rethrowUnchecked(final Exception ex)
    {
        Exceptions.<RuntimeException>rethrow(ex);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrow(final Exception ex)
            throws T
    {
        throw (T)ex;
    }

}
