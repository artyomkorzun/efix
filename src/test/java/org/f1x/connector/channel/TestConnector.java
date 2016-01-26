package org.f1x.connector.channel;

import org.f1x.connector.Connector;

public class TestConnector implements Connector {

    protected final Channel channel;

    public TestConnector(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public Channel connect() {
        return channel;
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean isConnectionPending() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

}
