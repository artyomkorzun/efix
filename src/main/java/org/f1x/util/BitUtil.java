package org.f1x.util;

public final class BitUtil {

    public static final String CACHE_LINE_LENGTH_PROP_KEY = "f1x.cache.line.length";
    public static final int CACHE_LINE_LENGTH = Integer.getInteger(CACHE_LINE_LENGTH_PROP_KEY, 64);

    public static final int SIZE_OF_BYTE = 1;
    public static final int SIZE_OF_BOOLEAN = 1;
    public static final int SIZE_OF_CHAR = 2;
    public static final int SIZE_OF_SHORT = 2;
    public static final int SIZE_OF_INT = 4;
    public static final int SIZE_OF_FLOAT = 4;
    public static final int SIZE_OF_LONG = 8;
    public static final int SIZE_OF_DOUBLE = 8;

    public static int findNextPowerOfTwo(int value) {
        return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
    }

    public static int align(int value, int alignment) {
        int mask = alignment - 1;
        return (value + mask) & ~mask;
    }

    public static boolean isEven(int value) {
        return (value & 1) == 0;
    }

    public static boolean isPowerOfTwo(int value) {
        return value > 0 && ((value & (~value + 1)) == value);
    }

    public static int next(int current, int max) {
        int next = current + 1;
        if (next == max)
            next = 0;

        return next;
    }

    public static int previous(int current, int max) {
        if (0 == current)
            return max - 1;

        return current - 1;
    }

    public static int calculateShift(int scale) {
        if (scale == 4)
            return 2;
        else if (scale == 8)
            return 3;

        throw new IllegalArgumentException("Unknown pointer size");
    }

}
