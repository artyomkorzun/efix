package org.efix.connector;

import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class InitiatorConnector extends SocketChannelConnector {

    protected final EpochClock clock;
    protected final int reconnectInterval;

    protected long errorTime = Long.MIN_VALUE;

    public InitiatorConnector(SocketAddress address, SocketOptions options, EpochClock clock, int reconnectInterval) {
        super(address, options);
        this.clock = clock;
        this.reconnectInterval = reconnectInterval;
    }

    @Override
    public void open() {
    }

    @Override
    protected NioSocketChannel doConnect() {
        long now = clock.time();
        if (canConnect(now)) {
            try {
                if (channel == null) {
                    channel = SocketChannel.open();
                    configure(channel);
                    channel.connect(address);
                }

                if (channel.finishConnect())
                    return new NioSocketChannel(channel);
            } catch (IOException e) {
                errorTime = now;
                disconnect();
                throw new ConnectionException(e);
            }
        }

        return null;
    }

    @Override
    public void close() {
        disconnect();
    }

    protected boolean canConnect(long now) {
        return now >= errorTime + reconnectInterval;
    }

}
