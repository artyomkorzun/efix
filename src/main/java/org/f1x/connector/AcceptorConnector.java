package org.f1x.connector;

import org.f1x.connector.channel.Channel;
import org.f1x.connector.channel.NioSocketChannel;
import org.f1x.connector.channel.SocketOptions;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class AcceptorConnector extends SocketChannelConnector {

    protected static final int PENDING_CONNECTIONS = 1;

    protected ServerSocketChannel acceptor;

    public AcceptorConnector(SocketAddress address, SocketOptions options) {
        super(address, options);
    }

    @Override
    public void open() {
        try {
            acceptor = ServerSocketChannel.open();
            acceptor.configureBlocking(false);
            acceptor.bind(address, PENDING_CONNECTIONS);
        } catch (IOException e) {
            close();
            throw new ConnectionException(e);
        }
    }

    @Override
    public void close() {
        disconnect();
        closeChannel(acceptor);
        acceptor = null;
    }

    @Override
    public Channel connect() {
        try {
            channel = acceptor.accept();
            if (channel == null)
                return null;

            configure(channel);
            return new NioSocketChannel(channel);
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }
    }


}
