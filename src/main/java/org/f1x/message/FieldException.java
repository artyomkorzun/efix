package org.f1x.message;

/**
 * Field specific exception.
 */
public class FieldException extends RuntimeException {

    protected final int field;

    public FieldException(int field, String message) {
        this.field = field;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
