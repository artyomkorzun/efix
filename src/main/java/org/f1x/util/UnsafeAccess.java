package org.f1x.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public final class UnsafeAccess {

    public static final Unsafe UNSAFE;

    static
    {
        Unsafe unsafe = null;
        try
        {
            PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>() {
                @Override
                public Unsafe run() throws Exception {
                    final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);

                    return (Unsafe)f.get(null);
                }
            };
            unsafe = AccessController.doPrivileged(action);
        }
        catch (final Exception ex)
        {
            Exceptions.rethrowUnchecked(ex);
        }

        UNSAFE = unsafe;
    }

    private UnsafeAccess() {
        throw new AssertionError("Not for you!");
    }

}
