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
            throw new ConnectionException("Connection is already initiated", channel);
        }

        try {
            channel = SocketChannel.open();
        } catch (IOException e) {
            disconnect();
            throw new ConnectionException(null, address, e);
        }

        try {
            configure(channel);
            channel.connect(address);
        } catch (IOException e) {
            ConnectionException exception = new ConnectionException(channel, e);
            disconnect();
            throw exception;
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        if (channel == null) {
            throw new ConnectionException("Connection is not initiated", null, address);
        }

        if (nioChannel != null) {
            throw new ConnectionException("Already connected", channel);
        }

        try {
            boolean finished = channel.finishConnect();
            if (finished) {
                nioChannel = new NioSocketChannel(channel);
            }
        } catch (IOException e) {
            ConnectionException exception = new ConnectionException(channel, e);
            disconnect();
            throw exception;
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
