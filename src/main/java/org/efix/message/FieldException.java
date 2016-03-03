package org.efix.message;

public class FieldException extends RuntimeException {

    protected final int field;

    public FieldException(int field, String message) {
        super(message);
        this.field = field;
    }

}
