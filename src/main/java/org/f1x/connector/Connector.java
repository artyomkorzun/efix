package org.f1x.connector;

import org.f1x.connector.channel.Channel;

public interface Connector extends AutoCloseable {

    Channel connect();

    void close();

}
