package org.efix.util.type;

public class DecimalType {

    public static final int MAX_DIGITS = 15;

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = MAX_DIGITS + 2;

    public static final int MAX_SCALE = 18;

    private static final long[] MULTIPLIER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L, 100000000000L,
            1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L};

    public static long multiplier(int scale) {
        return MULTIPLIER[scale];
    }

}
