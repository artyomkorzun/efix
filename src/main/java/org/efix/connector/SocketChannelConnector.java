package org.efix.connector;

import org.efix.connector.channel.SocketOptions;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;


public abstract class SocketChannelConnector implements Connector {

    protected final SocketAddress address;
    protected final SocketOptions options;

    protected final EpochClock clock;
    protected final int connectInterval;

    protected SocketChannel channel;
    protected long lastConnectTime = Long.MIN_VALUE;

    public SocketChannelConnector(SocketAddress address, SocketOptions options, EpochClock clock,  int connectInterval) {
        this.address = address;
        this.options = options;
        this.clock = clock;
        this.connectInterval = connectInterval;
    }

    @Override
    public boolean initiateConnect() throws ConnectionException {
        long now = clock.time();
        boolean canConnect = canConnect(now);
        if (canConnect) {
            lastConnectTime = now;
            doInitiateConnect();
        }

        return canConnect;
    }

    protected abstract void doInitiateConnect();

    protected void configure(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        for (int i = 0; i < options.size(); i++)
            setOption(options.get(i), channel);
    }

    protected static <T> void setOption(SocketOptions.Entry<T> entry, SocketChannel channel) throws IOException {
        channel.setOption(entry.option(), entry.value());
    }

    protected boolean canConnect(long now) {
        return now - connectInterval >= lastConnectTime;
    }

}
