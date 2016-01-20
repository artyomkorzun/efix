package org.f1x.connector;

import org.f1x.connector.channel.Channel;
import org.f1x.util.Disposable;

public interface Connector extends Disposable {

    void open() throws ConnectionException;

    void close();

    Channel connect() throws ConnectionException;

    void disconnect() throws ConnectionException;

    boolean isConnectionPending();

    boolean isConnected();

}
