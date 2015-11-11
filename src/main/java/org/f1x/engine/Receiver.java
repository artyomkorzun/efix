package org.f1x.engine;

import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.Fields;
import org.f1x.message.parser.MessageParser;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.concurrent.MessageHandler;

import static org.f1x.engine.SessionUtil.parseBeginString;
import static org.f1x.engine.SessionUtil.parseBodyLength;

public class Receiver {

    protected final MessageLog log;
    protected final MessageParser parser;
    protected final MutableBuffer buffer;

    protected Channel channel;
    protected int offset;

    public Receiver(MessageLog log, MessageParser parser, MutableBuffer buffer) {
        this.parser = parser;
        this.log = log;
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
        while (remaining >= Fields.MIN_MESSAGE_LENGTH) {
            int messageLength = parseMessageLength(buffer, offset, remaining);
            if (messageLength > remaining)
                break;

            onMessage(handler, buffer, offset, messageLength);

            offset += messageLength;
            remaining -= messageLength;
        }

        return length - remaining;
    }

    protected void onMessage(MessageHandler handler, Buffer buffer, int offset, int messageLength) {
        try {
            handler.onMessage(EventTypes.INBOUND_MESSAGE, buffer, offset, messageLength);
        } finally {
            log.log(true, buffer, offset, messageLength);
        }
    }

    protected int parseMessageLength(Buffer buffer, int offset, int length) {
        parser.wrap(buffer, offset, length);
        parseBeginString(parser);
        int bodyLength = parseBodyLength(parser);
        int headerLength = parser.fieldOffset() + parser.fieldLength() - offset;
        return headerLength + bodyLength + Fields.CHECK_SUM_FIELD_LENGTH;
    }

}
