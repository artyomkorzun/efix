package org.efix.connector;

import org.efix.util.SocketUtil;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;


public class ConnectionException extends RuntimeException {

    protected final SocketAddress localAddress;
    protected final SocketAddress remoteAddress;

    public ConnectionException(String message, SocketAddress local, SocketAddress remote) {
        this(message, local, remote, null);
    }

    public ConnectionException(String message, SocketChannel channel) {
        this(message, SocketUtil.getLocalAddress(channel), SocketUtil.getRemoteAddress(channel), null);
    }

    public ConnectionException(SocketAddress local, SocketAddress remote, Throwable cause) {
        this(cause.getMessage(), local, remote, cause);
    }

    public ConnectionException(SocketChannel channel, Throwable cause){
        this(cause.getMessage(), SocketUtil.getLocalAddress(channel), SocketUtil.getRemoteAddress(channel), cause);
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

    private static String compositeMessage(String message, SocketAddress local, SocketAddress remote){
        return String.format("Socket (local address %s, remote address %s). %s", local, remote, message);
    }

}
