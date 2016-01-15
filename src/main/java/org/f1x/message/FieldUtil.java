package org.f1x.message;


public class FieldUtil {

    public static final int MIN_MESSAGE_LENGTH = 63;
    public static final byte TAG_VALUE_SEPARATOR = '=';
    public static final byte FIELD_SEPARATOR = '\u0001';
    public static final int CHECK_SUM_FIELD_LENGTH = 7;

    public static int checkSum(int sum) {
        return sum & 0xFF;
    }

    public static int checkTag(int tag, int expected) {
        if (tag != expected)
            throw new FieldException(tag, String.format("Unexpected field %s, expected %s", tag, expected));

        return tag;
    }

    public static int checkPositive(int tag, int value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Non positive field %s value %s", tag, value));

        return value;
    }

    public static int checkNonNegative(int tag, int value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Non positive field %s value %s", tag, value));

        return value;
    }

    public static int checkPresent(int tag, int value, int nullValue) {
        if (value == nullValue)
            throw new FieldException(tag, String.format("Missing field %s", tag));

        return value;
    }

    public static <T extends CharSequence> T checkPresent(int tag, T value) {
        if (value.length() == 0)
            throw new FieldException(tag, String.format("Missing field %s", tag));

        return value;
    }
}
