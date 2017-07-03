package org.efix.session;

import org.efix.FixVersion;
import org.efix.connector.channel.Channel;
import org.efix.message.FieldException;
import org.efix.message.FieldUtil;
import org.efix.message.field.Tag;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;


public class Receiver {

    protected final MutableBuffer buffer;
    protected final int mtuSize;
    protected final int bodyLengthStart;

    protected Channel channel;
    protected int offset;

    public Receiver(FixVersion fixVersion, int bufferSize, int mtuSize) {
        this.bodyLengthStart = 5 + fixVersion.beginString().length();
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

            if (remaining > 0) {
                buffer.putBytes(0, buffer, offset, remaining);
            }

            this.offset = remaining;
        }

        return bytesReceived;
    }

    public void channel(Channel channel) {
        this.channel = channel;
        this.offset = 0;
    }

    protected int parseMessageLength(Buffer buffer, int offset, int length) {
        int bodyLengthOffset = offset + bodyLengthStart;
        if (buffer.getByte(bodyLengthOffset - 1) != '=') {
            throw new FieldException(Tag.BodyLength, "Expected '=' at BodyLength(9) field");
        }

        int bodyLength = 0;

        while (true) {
            byte b = buffer.getByte(bodyLengthOffset++);
            if (b == '\u0001') {
                break;
            }

            bodyLength = 10 * bodyLength + (b - '0');
        }

        int headerLength = bodyLengthOffset - offset;
        return headerLength + bodyLength + FieldUtil.CHECK_SUM_FIELD_LENGTH;
    }

    protected void checkMessageLength(int messageLength) {
        int capacity = buffer.capacity();
        if (messageLength > capacity) {
            throw new InsufficientSpaceException(String.format("Message length %s exceeds buffer size %s", messageLength, capacity));
        }
    }

}
