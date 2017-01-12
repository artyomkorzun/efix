package org.efix.connector;

import org.efix.connector.channel.SocketOptions;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;


public abstract class SocketChannelConnector implements Connector {

    protected final SocketAddress address;
    protected final SocketOptions options;

    protected final EpochClock clock;
    protected final int connectInterval;

    protected SocketChannel channel;
    protected long lastConnectTime = Long.MIN_VALUE;

    public SocketChannelConnector(SocketAddress address, SocketOptions options, EpochClock clock, int connectInterval) {
        this.address = address;
        this.options = options;
        this.clock = clock;
        this.connectInterval = connectInterval;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
        disconnect();
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

    protected boolean canConnect(long now) {
        return now - connectInterval >= lastConnectTime;
    }

    protected void configure(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        setOptions(options, channel);
    }

    protected static void setOptions(SocketOptions options, NetworkChannel channel) throws IOException {
        for (int i = 0; i < options.size(); i++) {
            setOption(options.get(i), channel);
        }
    }

    protected static <T> void setOption(SocketOptions.Entry<T> entry, NetworkChannel channel) throws IOException {
        channel.setOption(entry.option(), entry.value());
    }

}
