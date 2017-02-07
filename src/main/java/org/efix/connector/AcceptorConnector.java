package org.efix.connector;

import org.efix.connector.channel.Channel;
import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.connector.selector.NioSelectedKeySet;
import org.efix.connector.selector.Selector;
import org.efix.util.CloseHelper;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Uses selector to eliminate garbage on socket.accept().
 */
public class AcceptorConnector extends SocketChannelConnector {

    protected static final int MAX_PENDING_CONNECTIONS = 1;

    protected final SocketOptions acceptorOptions;

    protected Selector selector;
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
    public void open() {
        super.open();

        selector = new Selector();
    }

    @Override
    public void close() {
        super.close();

        CloseHelper.close(selector);
        selector = null;
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
            acceptor.register(selector.selector(), SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(address, null, e);
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected");
        }

        if (!isConnectionInitiated()) {
            throw new IllegalStateException("Connection is not initiated");
        }

        try {
            channel = accept();

            if (channel != null) {
                CloseHelper.close(acceptor);
                acceptor = null;
                selector.cancel();

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

            if (selector != null) {
                selector.cancel();
            }
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

    protected SocketChannel accept() throws IOException {
        SocketChannel channel = null;
        NioSelectedKeySet keys = selector.selectNow();

        if (!keys.isEmpty()) {
            int size = keys.size();

            if (size > 1) {
                throw new IllegalStateException("Selected keys more than one: " + size);
            }

            SelectionKey key = keys.keys()[0];

            if (!key.isAcceptable()) {
                throw new IllegalStateException("Selected key is not acceptable, readyOps: " + key.readyOps());
            }

            channel = acceptor.accept();

            if (channel == null) {
                throw new IllegalStateException("Channel is null");
            }
        }

        return channel;
    }

}
