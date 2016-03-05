package org.efix.util.type;

public class DoubleType {

    public static final int MAX_UINTEGER_LENGTH = 15;
    public static final int MAX_UDECIMAL_LENGTH = MAX_UINTEGER_LENGTH + 1;

    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = MAX_UDECIMAL_LENGTH + 1;

    public static final double MAX_VALUE = 1E15 - 1;
    public static final double MIN_VALUE = -MAX_VALUE;

}
