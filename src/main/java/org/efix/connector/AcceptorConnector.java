package org.efix.connector;

import org.efix.connector.channel.Channel;
import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.util.CloseHelper;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;


public class AcceptorConnector extends SocketChannelConnector {

    protected static final int MAX_PENDING_CONNECTIONS = 1;

    protected ServerSocketChannel acceptor;

    public AcceptorConnector(SocketAddress address, SocketOptions options, EpochClock clock, int connectInterval) {
        super(address, options, clock, connectInterval);
    }

    @Override
    public void doInitiateConnect() throws ConnectionException {
        if (acceptor != null) {
            throw new ConnectionException("Connection is already initiated");
        }

        try {
            acceptor = ServerSocketChannel.open();
            acceptor.bind(address, MAX_PENDING_CONNECTIONS);
            acceptor.configureBlocking(false);
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        if (acceptor == null) {
            throw new ConnectionException("Connection is not initiated");
        }

        if (channel != null) {
            throw new ConnectionException("Already connected");
        }

        NioSocketChannel nioChannel = null;
        try {
            channel = acceptor.accept();
            if (channel != null) {
                configure(channel);
                nioChannel = new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }

        return nioChannel;
    }

    @Override
    public void disconnect() throws ConnectionException {
        try {
            CloseHelper.close(acceptor);
            CloseHelper.close(channel);
        } finally {
            acceptor = null;
            channel = null;
        }
    }

    @Override
    public boolean isConnectionInitiated() {
        return acceptor != null;
    }

    @Override
    public boolean isConnectionPending() {
        return acceptor != null && channel == null;
    }

    @Override
    public boolean isConnected() {
        return channel != null;
    }

}
