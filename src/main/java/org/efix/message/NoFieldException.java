package org.efix.message;

public class NoFieldException extends FieldException {

    public NoFieldException(int field) {
        super(field, "Field " + field + " not found");
    }

}
