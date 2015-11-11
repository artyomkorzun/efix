package org.f1x.connector;

import org.f1x.connector.channel.NioSocketChannel;
import org.f1x.connector.channel.SocketOptions;
import org.f1x.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class InitiatorConnector extends SocketChannelConnector {

    protected final EpochClock clock;
    protected final int reconnectInterval;

    protected long errorTime;

    public InitiatorConnector(int reconnectInterval, EpochClock clock, SocketAddress address, SocketOptions options) {
        super(address, options);
        this.clock = clock;
        this.reconnectInterval = reconnectInterval;
        this.errorTime = -reconnectInterval;
    }

    @Override
    public void open() {
    }

    @Override
    protected void doConnect() {
        long now = clock.time();
        if (canConnect(now)) {
            try {
                if (channel == null) {
                    channel = SocketChannel.open();
                    configure(channel);
                    channel.connect(address);
                }

                if (channel.finishConnect())
                    nioChannel = new NioSocketChannel(channel);
            } catch (IOException e) {
                errorTime = now;
                disconnect();
                throw new ConnectionException(e);
            }
        }
    }

    @Override
    public void close() throws ConnectionException {
        disconnect();
    }

    protected boolean canConnect(long now) {
        return now - errorTime >= reconnectInterval;
    }

}
