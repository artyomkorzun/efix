package org.efix.message;

public class InvalidFieldException extends FieldException {

    public InvalidFieldException(int tag, String message) {
        super(tag, "Invalid field #" + tag + ": " + message);
    }
}
