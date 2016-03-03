package org.efix.util.type;

public class DoubleType {

    public static final int MAX_UNSIGNED_INTEGER_LENGTH = 15;
    public static final int MAX_NEGATIVE_INTEGER_LENGTH = MAX_UNSIGNED_INTEGER_LENGTH + 1;

    public static final int MAX_UNSIGNED_FRACTIONAL_LENGTH = MAX_UNSIGNED_INTEGER_LENGTH + 1;
    public static final int MAX_NEGATIVE_FRACTIONAL_LENGTH = MAX_UNSIGNED_FRACTIONAL_LENGTH + 1;

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = MAX_NEGATIVE_FRACTIONAL_LENGTH;

    public static final double MAX_VALUE = 1E15 - 1;
    public static final double MIN_VALUE = -MAX_VALUE;

}
