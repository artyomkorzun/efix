package org.f1x.connector;

import org.f1x.connector.channel.Channel;

public interface Connector extends AutoCloseable {

    void open() throws ConnectionException;

    void close() throws ConnectionException;

    Channel connect() throws ConnectionException;

    void disconnect() throws ConnectionException;

    boolean isConnectionPending();

    boolean isConnected();

}
