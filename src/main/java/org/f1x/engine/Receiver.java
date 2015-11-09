package org.f1x.engine;

import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.parser.DefaultMessageParser;
import org.f1x.util.Fields;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.concurrent.Reader;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.f1x.message.parser.MessageParsers.parseBeginString;
import static org.f1x.message.parser.MessageParsers.parseBodyLength;

public class Receiver {

    protected final MessageLog log;

    protected final DefaultMessageParser parser = new DefaultMessageParser();
    protected final ByteBuffer byteBuffer;
    protected final MutableBuffer buffer;

    protected Channel channel;

    public Receiver(MessageLog log) {
        this.log = log;
        this.byteBuffer = ByteBuffer.allocateDirect(1 << 20);
        this.buffer = new UnsafeBuffer(byteBuffer);
    }

    public int receive(Reader reader) throws IOException {
        Channel channel = this.channel;
        if (channel == null)
            return 0;

        ByteBuffer byteBuffer = this.byteBuffer;
        int bytesRead = channel.read(byteBuffer);
        if (bytesRead > 0) {
            int position = byteBuffer.position();
            int bytesProcessed = processMessages(reader, buffer, 0, position);
            if (bytesProcessed != 0) {
                byteBuffer.limit(position).position(bytesProcessed);
                byteBuffer.compact();
            }
        }

        return bytesRead;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected int processMessages(Reader reader, Buffer buffer, int offset, int length) {
        int remaining = length;
        while (remaining >= Fields.MIN_MESSAGE_LENGTH) {
            int messageLength = parseMessageLength(buffer, offset, remaining);
            if (messageLength > remaining)
                break;

            onMessage(reader, buffer, offset, messageLength);

            offset += messageLength;
            remaining -= messageLength;
        }

        return length - remaining;
    }

    protected void onMessage(Reader reader, Buffer buffer, int offset, int messageLength) {
        try {
            reader.read(EventTypes.INBOUND_MESSAGE, buffer, offset, messageLength);
        } finally {
            log.log(true);
        }
    }

    protected int parseMessageLength(Buffer buffer, int offset, int length) {
        DefaultMessageParser parser = this.parser.wrap(buffer, offset, length);
        parseBeginString(parser, null);
        int bodyLength = parseBodyLength(parser);
        int headerLength = parser.getOffset() - offset;
        return headerLength + bodyLength + Fields.CHECK_SUM_FIELD_LENGTH;
    }

}
