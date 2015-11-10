package org.f1x.connector;

public class ConnectionException extends RuntimeException {

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
