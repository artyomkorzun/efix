package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public class Sender {

    protected final FIXVersion version;
    protected final SessionID sessionID;
    protected final MessageLog log;
    protected final MessageBuilder builder;
    protected final MutableBuffer buffer;

    protected Channel channel;

    public Sender(FIXVersion version, SessionID sessionID, MessageLog log, MessageBuilder builder, MutableBuffer buffer) {
        this.version = version;
        this.sessionID = sessionID;
        this.log = log;
        this.builder = builder.wrap(buffer);
        this.buffer = buffer;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void send(Buffer buffer) {
        send(buffer, 0, buffer.capacity());
    }

    public void send(Buffer buffer, int offset, int length) {
        try {
            int written = 0;
            while ((written += channel.write(buffer, offset + written, length - written)) < length)
                Thread.yield();
        } finally {
            log.log(false, buffer, offset, length);
        }
    }

}
