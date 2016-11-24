package org.efix.connector;

import org.efix.connector.channel.Channel;


public interface Connector {

    void initiateConnect() throws ConnectionException;

    Channel finishConnect() throws ConnectionException;

    void disconnect() throws ConnectionException;

    boolean isConnectionInitiated();

    boolean isConnectionPending();

    boolean isConnected();

}
