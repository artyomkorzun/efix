package org.efix.connector;

import java.net.SocketAddress;


public class ConnectionException extends RuntimeException {

    protected final SocketAddress localAddress;
    protected final SocketAddress remoteAddress;

    public ConnectionException(String message, SocketAddress local, SocketAddress remote) {
        this(message, local, remote, null);
    }

    public ConnectionException(SocketAddress local, SocketAddress remote, Throwable cause) {
        this(cause.getMessage(), local, remote, cause);
    }


    public ConnectionException(String message, SocketAddress local, SocketAddress remote, Throwable cause) {
        super(compositeMessage(message, local, remote), cause);

        this.localAddress = local;
        this.remoteAddress = remote;
    }

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    private static String compositeMessage(String message, SocketAddress local, SocketAddress remote) {
        return String.format("Socket (local address: %s, remote address: %s). %s", local, remote, message);
    }

}
