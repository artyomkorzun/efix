package org.f1x.engine;

import org.f1x.connector.channel.Channel;
import org.f1x.util.buffer.Buffer;

public class Sender {

    protected Channel channel;

    public void setChannel(Channel channel) {
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
