package org.efix.connector;

import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class InitiatorConnector extends SocketChannelConnector {

    protected final EpochClock clock;
    protected final int connectInterval;

    protected long connectTime = Long.MIN_VALUE;

    public InitiatorConnector(SocketAddress address, SocketOptions options, EpochClock clock, int connectInterval) {
        super(address, options);
        this.clock = clock;
        this.connectInterval = connectInterval;
    }

    @Override
    public void open() {
    }

    @Override
    protected NioSocketChannel doConnect() {
        NioSocketChannel nioChannel = null;

        try {
            if (channel == null) {
                long now = clock.time();

                if (canConnect(now)) {
                    connectTime = now;

                    channel = SocketChannel.open();
                    configure(channel);
                    if (channel.connect(address))
                        nioChannel = new NioSocketChannel(channel);
                }
            } else {
                if (channel.finishConnect())
                    nioChannel = new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }

        return nioChannel;
    }

    @Override
    public void close() {
        disconnect();
    }

    protected boolean canConnect(long now) {
        return now >= connectTime + connectInterval;
    }

}
