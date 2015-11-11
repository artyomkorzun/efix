package org.f1x.message;

import org.f1x.message.fields.FixTags;
import org.f1x.util.ByteSequence;
import org.f1x.util.format.IntFormatter;

public class Fields {

    public static final int MIN_MESSAGE_LENGTH = 63;
    public static final byte TAG_VALUE_SEPARATOR = '=';
    public static final int TAG_VALUE_SEPARATOR_LENGTH = 1;
    public static final byte FIELD_SEPARATOR = '\u0001';
    public static final int FIELD_SEPARATOR_LENGTH = 1;
    public static final int FIELD_SEPARATOR_CHECK_SUM = FIELD_SEPARATOR;
    public static final int CHECK_SUM_FIELD_LENGTH = fieldLength(FixTags.CheckSum, "???");

    public static int nullableFieldLength(int field, CharSequence value) {
        return value == null ? 0 : fieldLength(field, value);
    }

    public static int fieldLength(int field, CharSequence value) {
        return tagLength(field) + TAG_VALUE_SEPARATOR_LENGTH + value.length() + FIELD_SEPARATOR_LENGTH;
    }

    public static int tagWithSeparatorLength(int field) {
        return tagLength(field) + TAG_VALUE_SEPARATOR_LENGTH;
    }

    public static int tagLength(int field) {
        return IntFormatter.stringSize(field);
    }

    public static int checkSum(int sum) {
        return sum & 0xFF;
    }

    public static int checkPositive(int field, int value) {
        if (value <= 0)
            throw new FieldException(field, String.format("Non positive field %s value %s", field, value));

        return value;
    }

    public static int checkNonNegative(int field, int value) {
        if (value < 0)
            throw new FieldException(field, String.format("Non positive field %s value %s", field, value));

        return value;
    }

    public static int checkPresent(int field, int value, int nullValue) {
        if (value == nullValue)
            throw new FieldException(field, String.format("Missing field %s", field));

        return value;
    }

    public static ByteSequence checkPresent(int field, ByteSequence value) {
        if (value.isEmpty())
            throw new FieldException(field, String.format("Missing field %s", field));

        return value;
    }
}
