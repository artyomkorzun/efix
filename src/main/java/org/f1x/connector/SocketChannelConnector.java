package org.f1x.connector;

import org.f1x.connector.channel.SocketOptions;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

public abstract class SocketChannelConnector implements Connector {

    protected final SocketAddress address;
    protected final SocketOptions options;

    protected SocketChannel channel;

    public SocketChannelConnector(SocketAddress address, SocketOptions options) {
        this.address = address;
        this.options = options;
    }

    @Override
    public void disconnect() {
        closeChannel(channel);
        channel = null;
    }

    @Override
    public boolean connectionPending() {
        return channel != null && channel.isConnectionPending();
    }

    protected void configure(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        for (int i = 0; i < options.size(); i++)
            setOption(options.get(i), channel);
    }

    protected <T> void setOption(SocketOptions.Entry<T> entry, SocketChannel channel) throws IOException {
        channel.setOption(entry.option(), entry.value());
    }

    protected void closeChannel(Channel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new ConnectionException(e);
            }
        }
    }

}
