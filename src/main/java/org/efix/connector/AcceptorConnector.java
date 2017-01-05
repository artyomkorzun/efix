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

    protected final SocketOptions acceptorOptions;

    protected ServerSocketChannel acceptor;

    public AcceptorConnector(SocketAddress address,
                             SocketOptions acceptorOptions,
                             SocketOptions channelOptions,
                             EpochClock clock,
                             int connectInterval) {
        super(address, channelOptions, clock, connectInterval);

        this.acceptorOptions = acceptorOptions;
    }

    @Override
    public void doInitiateConnect() throws ConnectionException {
        if (acceptor != null) {
            throw new IllegalStateException("Connection is already initiated");
        }

        try {
            acceptor = ServerSocketChannel.open();
            configure(acceptor);
            acceptor.bind(address, MAX_PENDING_CONNECTIONS);
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(address, null, e);
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        if (acceptor == null) {
            throw new IllegalStateException("Connection is not initiated");
        }

        if (channel != null) {
            throw new IllegalStateException("Already connected");
        }

        try {
            channel = acceptor.accept();
            if (channel != null) {
                configure(channel);
                return new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(address, null, e);
        }

        return null;
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

    protected void configure(ServerSocketChannel acceptor) throws IOException {
        acceptor.configureBlocking(false);
        setOptions(acceptorOptions, acceptor);
    }

}
