package org.f1x.message.parser;

public class FixParserException extends RuntimeException {

    static final long serialVersionUID = 1L;

    public FixParserException(String message) {
        super(message);
    }

    public FixParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
