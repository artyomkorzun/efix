package org.efix.engine;

import org.efix.connector.channel.Channel;
import org.efix.message.FieldUtil;
import org.efix.message.parser.FastMessageParser;
import org.efix.message.parser.MessageParser;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;

import static org.efix.engine.SessionUtil.parseBeginString;
import static org.efix.engine.SessionUtil.parseBodyLength;


public class Receiver {

    protected final MessageParser parser;
    protected final MutableBuffer buffer;
    protected final int mtuSize;

    protected Channel channel;
    protected int offset;

    public Receiver(int bufferSize, int mtuSize) {
        this.parser = new FastMessageParser();
        this.buffer = UnsafeBuffer.allocateDirect(bufferSize);
        this.mtuSize = mtuSize;
    }

    public int receive(MessageHandler handler) {
        MutableBuffer buffer = this.buffer;
        int offset = this.offset;
        int available = Math.min(buffer.capacity() - offset, mtuSize);

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

                handler.onMessage(buffer, offset, length);

                remaining -= length;
                offset += length;
            }

            if (remaining > 0)
                buffer.putBytes(0, buffer, offset, remaining);

            this.offset = remaining;
        }

        return bytesReceived;
    }

    public void channel(Channel channel) {
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
