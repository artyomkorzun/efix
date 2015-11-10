package org.f1x.connector;

import org.f1x.connector.channel.Channel;
import org.f1x.connector.channel.NioSocketChannel;
import org.f1x.connector.channel.SocketOptions;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class InitiatorConnector extends SocketChannelConnector {

    public InitiatorConnector(SocketAddress address, SocketOptions options) {
        super(address, options);
    }

    @Override
    public void open() {
    }

    @Override
    public Channel connect() {
        try {
            if (channel == null) {
                channel = SocketChannel.open();
                configure(channel);
                channel.connect(address);
            }

            return channel.finishConnect() ? new NioSocketChannel(channel) : null;
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }
    }

    @Override
    public void close() throws ConnectionException {
        disconnect();
    }

}
