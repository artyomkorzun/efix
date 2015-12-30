package org.f1x.engine;

import org.f1x.connector.channel.Channel;
import org.f1x.message.FieldUtil;
import org.f1x.message.parser.MessageParser;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.concurrent.MessageHandler;

import static org.f1x.engine.SessionUtil.parseBeginString;
import static org.f1x.engine.SessionUtil.parseBodyLength;

public class Receiver {

    protected final MessageParser parser;
    protected final MutableBuffer buffer;

    protected Channel channel;
    protected int offset;

    public Receiver(MessageParser parser, MutableBuffer buffer) {
        this.parser = parser;
        this.buffer = buffer;
    }

    public int receive(MessageHandler handler) {
        Channel channel = this.channel;
        MutableBuffer buffer = this.buffer;
        int offset = this.offset;
        int bytesRead = channel.read(buffer, offset, buffer.capacity() - offset);
        if (bytesRead > 0) {
            int length = offset + bytesRead;
            int bytesProcessed = processMessages(handler, buffer, length);
            if (bytesProcessed != 0) {
                int remaining = length - bytesProcessed;
                this.offset = remaining;
                if (remaining > 0)
                    buffer.putBytes(0, buffer, bytesProcessed, remaining);
            }
        }

        return bytesRead;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected int processMessages(MessageHandler handler, Buffer buffer, int length) {
        int offset = 0;
        int remaining = length;
        while (remaining >= FieldUtil.MIN_MESSAGE_LENGTH) {
            int messageLength = parseMessageLength(buffer, offset, remaining);
            if (messageLength > remaining)
                break;

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
        int headerLength = parser.fieldOffset() + parser.fieldLength() - offset;
        return headerLength + bodyLength + FieldUtil.CHECK_SUM_FIELD_LENGTH;
    }

}
