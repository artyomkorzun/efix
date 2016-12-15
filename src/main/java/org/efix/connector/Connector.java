package org.efix.connector;

import org.efix.connector.channel.Channel;


public interface Connector {

    /**
     * In some case connector can't initiate connect at once.
     * In this case you should initiate connect again.
     * The current implementation uses connect interval to defend on frequent unsuccessful connects.
     *
     * @return true if connection initiated otherwise false.
     */
    boolean initiateConnect() throws ConnectionException;

    /**
     * Uses only after successful initiating connection.
     *
     * @return nullable channel if connection is not established otherwise false.
     */
    Channel finishConnect() throws ConnectionException;

    void disconnect() throws ConnectionException;

    boolean isConnectionInitiated();

    boolean isConnectionPending();

    boolean isConnected();

}
