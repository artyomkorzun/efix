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

        int bytesReceived = channel.read(buffer, offset, buffer.capacity() - offset);
        if (bytesReceived > 0) {
            int length = offset + bytesReceived;
            int bytesRead = processMessages(handler, buffer, length);
            if (bytesRead > 0) {
                int remaining = length - bytesRead;
                if (remaining > 0)
                    buffer.putBytes(0, buffer, bytesRead, remaining);

                this.offset = remaining;
            }
        }

        return bytesReceived;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected int processMessages(MessageHandler handler, Buffer buffer, int length) {
        int offset = 0;
        int remaining = length;
        while (remaining >= FieldUtil.MIN_MESSAGE_LENGTH) {
            int messageLength = parseMessageLength(buffer, offset, remaining);
            if (messageLength > remaining) {
                checkMessageLength(messageLength);
                break;
            }

            handler.onMessage(EventTypes.INBOUND_MESSAGE, buffer, offset, messageLength);

            offset += messageLength;
            remaining -= messageLength;
        }

        return length - remaining;
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
