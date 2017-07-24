package org.efix.connector;

import org.efix.connector.channel.Channel;


public class FailoverConnector implements Connector {

    protected final Connector[] connectors;

    protected int current;
    protected int next;

    public FailoverConnector(Connector... connectors) {
        if (connectors.length == 0) {
            throw new IllegalArgumentException("No connectors");
        }

        this.connectors = connectors;
    }

    @Override
    public void open() {
        for (Connector connector : connectors) {
            connector.open();
        }
    }

    @Override
    public void close() {
        for (Connector connector : connectors) {
            connector.close();
        }
    }

    @Override
    public boolean initiateConnect() throws ConnectionException {
        try {
            return connectors[current].initiateConnect();
        } catch (Throwable e) {
            // next();
            throw e;
        }
    }

    @Override
    public Channel finishConnect() throws ConnectionException {
        try {
            return connectors[current].finishConnect();
        } catch (Throwable e) {
            // next();
            throw e;
        }
    }

    @Override
    public void disconnect() throws ConnectionException {
        connectors[current].disconnect();
        current = next;
    }

    @Override
    public boolean isConnectionInitiated() {
        return connectors[current].isConnectionInitiated();
    }

    @Override
    public boolean isConnectionPending() {
        return connectors[current].isConnectionPending();
    }

    @Override
    public boolean isConnected() {
        return connectors[current].isConnected();
    }

    public void next() {
        if (++next == connectors.length) {
            next = 0;
        }
    }

    public void next(int next) {
        next = next;
    }

    public int size() {
        return connectors.length;
    }

}
