package org.efix.connector;

import org.efix.connector.channel.Channel;
import org.efix.connector.channel.NioSocketChannel;
import org.efix.connector.channel.SocketOptions;
import org.efix.util.CloseHelper;
import org.efix.util.EpochClock;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;


public class InitiatorConnector extends SocketChannelConnector {

    protected NioSocketChannel nioChannel;

    public InitiatorConnector(SocketAddress address, SocketOptions options, EpochClock clock, int connectInterval) {
        super(address, options, clock, connectInterval);
    }

    @Override
    public void doInitiateConnect() throws ConnectionException {
        if (channel != null) {
            throw new ConnectionException("Connection is already initiated");
        }

        try {
            channel = SocketChannel.open();
            configure(channel);
            channel.connect(address);
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        if (channel == null) {
            throw new ConnectionException("Connection is not initiated");
        }

        if (nioChannel != null) {
            throw new ConnectionException("Already connected");
        }

        try {
            boolean finished = channel.finishConnect();
            if (finished) {
                nioChannel = new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(e);
        }

        return nioChannel;
    }

    @Override
    public void disconnect() throws ConnectionException {
        try {
            CloseHelper.close(channel);
        } finally {
            channel = null;
            nioChannel = null;
        }
    }

    @Override
    public boolean isConnectionInitiated() {
        return channel != null;
    }

    @Override
    public boolean isConnectionPending() {
        return channel != null && nioChannel == null;
    }

    @Override
    public boolean isConnected() {
        return nioChannel != null;
    }

}
