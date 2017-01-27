package org.efix.engine;

import org.efix.connector.channel.Channel;
import org.efix.util.EpochClock;
import org.efix.util.buffer.Buffer;


public class Sender {

    protected final EpochClock clock;
    protected final int sendTimeoutMs;

    protected Channel channel;

    public Sender(EpochClock clock, int sendTimeoutMs) {
        this.clock = clock;
        this.sendTimeoutMs = sendTimeoutMs;
    }

    public void channel(Channel channel) {
        this.channel = channel;
    }

    public void send(Buffer buffer, int offset, int length) {
        int sent = channel.write(buffer, offset, length);

        if (sent < length) {
            long endMs = clock.time() + sendTimeoutMs;

            do {
                long nowMs = clock.time();

                if (nowMs > endMs) {
                    throw new TimeoutException(String.format("Send timeout %s ms.", sendTimeoutMs));
                }

                sent += channel.write(buffer, offset + sent, length - sent);

            } while (sent < length);
        }
    }

}
