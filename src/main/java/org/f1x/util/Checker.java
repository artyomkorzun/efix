package org.f1x.util;

import org.f1x.message.FieldException;
import org.f1x.util.buffer.Buffer;

import java.nio.ByteBuffer;
import java.util.Objects;

class BoundChecker {

    private static final boolean CHECK_BOUNDS = Registry.getBooleanValue(Registry.CHECK_BOUNDS_ENTRY_KEY, true);

    public static void boundsCheck(Buffer buffer, int offset, int length) {
        if (CHECK_BOUNDS)
            strictBoundsCheck(offset, length, buffer.capacity());
    }

    public static void boundsCheck(ByteBuffer buffer, int offset, int length) {
        if (CHECK_BOUNDS)
            strictBoundsCheck(offset, length, buffer.capacity());
    }

    public static void boundsCheck(byte[] buffer, int offset, int length) {
        if (CHECK_BOUNDS)
            strictBoundsCheck(offset, length, buffer.length);
    }

    public static void boundsCheck(int offset, int length, int capacity) {
        if (CHECK_BOUNDS)
            strictBoundsCheck(offset, length, capacity);
    }

    public static void strictBoundsCheck(int offset, int length, int capacity) {
        if ((offset | length | (offset + length) | (capacity - (offset + length))) < 0)
            throw new IndexOutOfBoundsException("offset=" + offset + " and length=" + length + " not valid for capacity=" + capacity);
    }

}

class MessageChecker extends BoundChecker {

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

    public static ByteArrayReference checkPresent(int field, ByteArrayReference value) {
        if (value.isEmpty())
            throw new FieldException(field, String.format("Missing field %s", field));

        return value;
    }


}

public class Checker extends MessageChecker {


    public static <T> T checkNotNull(T object) {
        return Objects.requireNonNull(object);
    }

}