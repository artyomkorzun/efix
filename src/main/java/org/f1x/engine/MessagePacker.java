package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.message.FieldUtil;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.FixTags;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.format.IntFormatter;
import org.f1x.util.format.TimestampFormatter;

public class MessagePacker {

    protected final String beginString;
    protected final SessionID sessionID;
    protected final MessageBuilder builder;
    protected final MutableBuffer buffer;
    protected final MutableBuffer wrapper;

    public MessagePacker(FIXVersion version, SessionID sessionID, MessageBuilder builder, MutableBuffer buffer) {
        this.beginString = version.getBeginString();
        this.sessionID = sessionID;
        this.builder = builder;
        this.buffer = buffer;
        this.wrapper = new UnsafeBuffer(buffer);
    }

    public Buffer pack(int msgSeqNum, long sendingTime, CharSequence msgType,
                       Buffer body, int offset, int length) {
        int bodyLength = computeBodyLength(msgSeqNum, sendingTime, msgType, length);
        int messageLength = computeMessageLength(bodyLength);
        checkMessageLength(messageLength);

        builder.wrap(buffer);
        addStandardHeader(bodyLength, msgSeqNum, sendingTime, msgType, builder);
        builder.appendBytes(body, offset, length);
        addCheckSum(builder);

        wrapper.wrap(buffer, 0, builder.length());
        return wrapper;
    }

    public Buffer pack(int msgSeqNum, long sendingTime, long origSendingTime,
                       CharSequence msgType, Buffer body, int offset, int length) {

        int bodyLength = computeBodyLength(msgSeqNum, sendingTime, origSendingTime, msgType, length);
        int messageLength = computeMessageLength(bodyLength);
        checkMessageLength(messageLength);

        builder.wrap(buffer);
        addStandardHeader(bodyLength, msgSeqNum, sendingTime, msgType, builder);
        builder.addTimestamp(FixTags.OrigSendingTime, origSendingTime);
        builder.addBoolean(FixTags.PossDupFlag, true);
        builder.appendBytes(body, offset, length);
        addCheckSum(builder);

        wrapper.wrap(buffer, 0, builder.length());
        return wrapper;
    }

    protected void addStandardHeader(int bodyLength, int msgSeqNum, long sendingTime, CharSequence msgType, MessageBuilder builder) {
        builder.addCharSequence(FixTags.BeginString, beginString);
        builder.addInt(FixTags.BodyLength, bodyLength);
        builder.addCharSequence(FixTags.MsgType, msgType);
        builder.addInt(FixTags.MsgSeqNum, msgSeqNum);

        builder.addCharSequence(FixTags.SenderCompID, sessionID.getSenderCompId());
        if (sessionID.getSenderSubId() != null)
            builder.addCharSequence(FixTags.SenderSubID, sessionID.getSenderSubId());

        builder.addCharSequence(FixTags.TargetCompID, sessionID.getTargetCompId());
        if (sessionID.getTargetSubId() != null)
            builder.addCharSequence(FixTags.TargetSubID, sessionID.getTargetSubId());

        builder.addTimestamp(FixTags.SendingTime, sendingTime);
    }

    protected void addCheckSum(MessageBuilder builder) {
        int checkSum = computeCheckSum(buffer, 0, builder.length());
        builder.startField(FixTags.CheckSum);
        if (checkSum < 100) {
            builder.appendChar('0');
            if (checkSum < 10)
                builder.appendChar('0');
        }

        builder.appendInt(checkSum).endField();
    }

    protected int computeBodyLength(int msgSeqNum, long sendingTime, long origSendingTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += computeBodyLength(msgSeqNum, sendingTime, msgType, length);
        bodyLength += 5 + TimestampFormatter.TIMESTAMP_LENGTH;
        bodyLength += 5;

        return bodyLength;
    }

    protected int computeBodyLength(int msgSeqNum, long sendingTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + IntFormatter.uintLength(msgSeqNum);
        bodyLength += 4 + sessionID.getSenderCompId().length();
        if (sessionID.getSenderSubId() != null)
            bodyLength += 4 + sessionID.getSenderSubId().length();

        bodyLength += 4 + sessionID.getTargetCompId().length();
        if (sessionID.getTargetSubId() != null)
            bodyLength += 4 + sessionID.getTargetSubId().length();

        bodyLength += 4 + TimestampFormatter.TIMESTAMP_LENGTH;
        bodyLength += length;

        return bodyLength;
    }

    protected int computeMessageLength(int bodyLength) {
        return 3 + beginString.length() +
                3 + IntFormatter.uintLength(bodyLength) +
                bodyLength +
                FieldUtil.CHECK_SUM_FIELD_LENGTH;
    }

    protected int computeCheckSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return FieldUtil.checkSum(sum);
    }

    protected void checkMessageLength(int length) {
        int capacity = buffer.capacity();
        if (length > capacity)
            throw new IllegalArgumentException(String.format("Message length %s exceeds buffer capacity %s", length, capacity));
    }

}
