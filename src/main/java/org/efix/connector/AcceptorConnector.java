package org.efix.connector;

import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.util.CloseHelper;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class AcceptorConnector extends SocketChannelConnector {

    protected static final int MAX_PENDING_CONNECTIONS = 1;

    protected ServerSocketChannel acceptor;

    public AcceptorConnector(SocketAddress address, SocketOptions options) {
        super(address, options);
    }

    @Override
    public void open() {
        try {
            acceptor = ServerSocketChannel.open();
            acceptor.configureBlocking(false);
            acceptor.bind(address, MAX_PENDING_CONNECTIONS);
        } catch (IOException e) {
            close();
            throw new ConnectionException(e);
        }
    }

    @Override
    public void close() {
        try {
            disconnect();
        } finally {
            ServerSocketChannel acceptor = this.acceptor;
            this.acceptor = null;
            CloseHelper.close(acceptor);
        }
    }

    @Override
    protected NioSocketChannel doConnect() {
        try {
            channel = acceptor.accept();
            if (channel != null) {
                configure(channel);
                return new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }

        return null;
    }

}
