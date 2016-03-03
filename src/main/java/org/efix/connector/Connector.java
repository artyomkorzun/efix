package org.efix.connector;

import org.efix.connector.channel.Channel;
import org.efix.util.Disposable;

public interface Connector extends Disposable {

    void open() throws ConnectionException;

    void close();

    Channel connect() throws ConnectionException;

    void disconnect() throws ConnectionException;

    boolean isConnectionPending();

    boolean isConnected();

}
