package org.f1x.engine;

import org.f1x.connector.channel.Channel;
import org.f1x.message.FieldUtil;
import org.f1x.message.parser.FastMessageParser;
import org.f1x.message.parser.MessageParser;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.concurrent.buffer.MessageHandler;

import static org.f1x.engine.SessionUtil.parseBeginString;
import static org.f1x.engine.SessionUtil.parseBodyLength;

public class Receiver {

    protected final MessageParser parser;
    protected final MutableBuffer buffer;

    protected Channel channel;
    protected int offset;

    public Receiver(int bufferSize) {
        this.parser = new FastMessageParser();
        this.buffer = UnsafeBuffer.allocateDirect(bufferSize);
    }

    public int receive(MessageHandler handler) {
        MutableBuffer buffer = this.buffer;
        int offset = this.offset;
        int available = buffer.capacity() - offset;

        int bytesReceived = channel.read(buffer, offset, available);

        if (bytesReceived > 0) {
            int remaining = offset + bytesReceived;
            offset = 0;

            while (remaining >= FieldUtil.MIN_MESSAGE_LENGTH) {
                int length = parseMessageLength(buffer, offset, remaining);
                if (length > remaining) {
                    checkMessageLength(length);
                    break;
                }

                handler.onMessage(EventType.INBOUND_MESSAGE, buffer, offset, length);

                remaining -= length;
                offset += length;
            }

            if (remaining > 0)
                buffer.putBytes(0, buffer, offset, remaining);

            this.offset = remaining;
        }

        return bytesReceived;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        this.offset = 0;
    }

    protected int parseMessageLength(Buffer buffer, int offset, int length) {
        parser.wrap(buffer, offset, length);
        parseBeginString(parser);
        int bodyLength = parseBodyLength(parser);
        int headerLength = parser.offset() - offset;
        return headerLength + bodyLength + FieldUtil.CHECK_SUM_FIELD_LENGTH;
    }

    protected void checkMessageLength(int messageLength) {
        int capacity = buffer.capacity();
        if (messageLength > capacity)
            throw new InsufficientSpaceException(String.format("Message length %s exceeds buffer size %s", messageLength, capacity));
    }

}
