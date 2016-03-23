package org.efix.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class UnsafeAccess {

    public static final Unsafe UNSAFE;

    static {
        Unsafe unsafe = null;
        try {
            PrivilegedExceptionAction<Unsafe> action = () -> {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe) f.get(null);
            };
            unsafe = AccessController.doPrivileged(action);
        } catch (final Exception ex) {
            LangUtil.rethrow(ex);
        }

        UNSAFE = unsafe;
    }

}
