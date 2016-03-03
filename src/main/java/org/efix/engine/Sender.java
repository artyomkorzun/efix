package org.efix.engine;

import org.efix.connector.channel.Channel;
import org.efix.util.buffer.Buffer;

public class Sender {

    protected Channel channel;

    public void channel(Channel channel) {
        this.channel = channel;
    }

    public void send(Buffer buffer, int offset, int length) {
        int bytesSent = 0;
        while (true) {
            bytesSent += channel.write(buffer, offset + bytesSent, length - bytesSent);
            if (bytesSent >= length)
                break;

            Thread.yield();
        }
    }

}
