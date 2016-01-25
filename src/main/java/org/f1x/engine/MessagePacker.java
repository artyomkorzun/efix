package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.message.FieldUtil;
import org.f1x.message.builder.FastMessageBuilder;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.field.Tag;
import org.f1x.util.ByteSequence;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.format.IntFormatter;
import org.f1x.util.type.TimestampType;


public class MessagePacker {

    protected final MessageBuilder builder = new FastMessageBuilder();
    protected final ByteSequence beginString;
    protected final SessionID sessionID;
    protected final MutableBuffer buffer;

    public MessagePacker(FIXVersion FIXVersion, SessionID sessionID, MutableBuffer buffer) {
        this.beginString = FIXVersion.beginString();
        this.sessionID = sessionID;
        this.buffer = buffer;
    }

    public int pack(int msgSeqNum, long time, ByteSequence msgType,
                    Buffer body, int offset, int length) {
        int bodyLength = computeBodyLength(msgSeqNum, time, msgType, length);
        int messageLength = computeMessageLength(bodyLength);
        checkMessageLength(messageLength);

        builder.wrap(buffer);
        addStandardHeader(bodyLength, msgSeqNum, time, msgType, builder);
        builder.appendBytes(body, offset, length);
        addCheckSum(builder);

        return messageLength;
    }

    public int pack(int msgSeqNum, long time, long origTime,
                    ByteSequence msgType, Buffer body, int offset, int length) {

        int bodyLength = computeBodyLength(msgSeqNum, time, origTime, msgType, length);
        int messageLength = computeMessageLength(bodyLength);
        checkMessageLength(messageLength);

        builder.wrap(buffer);
        addStandardHeader(bodyLength, msgSeqNum, time, msgType, builder);
        builder.addTimestamp(Tag.OrigSendingTime, origTime);
        builder.addBoolean(Tag.PossDupFlag, true);
        builder.appendBytes(body, offset, length);
        addCheckSum(builder);

        return messageLength;
    }

    protected void addStandardHeader(int bodyLength, int msgSeqNum, long time, CharSequence msgType, MessageBuilder builder) {
        builder.addByteSequence(Tag.BeginString, beginString);
        builder.addInt(Tag.BodyLength, bodyLength);
        builder.addCharSequence(Tag.MsgType, msgType);
        builder.addInt(Tag.MsgSeqNum, msgSeqNum);

        builder.addByteSequence(Tag.SenderCompID, sessionID.senderCompId());
        if (sessionID.senderSubId() != null)
            builder.addByteSequence(Tag.SenderSubID, sessionID.senderSubId());

        builder.addByteSequence(Tag.TargetCompID, sessionID.targetCompId());
        if (sessionID.targetSubId() != null)
            builder.addByteSequence(Tag.TargetSubID, sessionID.targetSubId());

        builder.addTimestamp(Tag.SendingTime, time);
    }

    protected void addCheckSum(MessageBuilder builder) {
        int checkSum = computeCheckSum(buffer, 0, builder.length());
        builder.startField(Tag.CheckSum);
        if (checkSum < 100) {
            builder.appendChar('0');
            if (checkSum < 10)
                builder.appendChar('0');
        }

        builder.appendInt(checkSum).endField();
    }

    protected int computeBodyLength(int msgSeqNum, long time, long origTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += computeBodyLength(msgSeqNum, time, msgType, length);
        bodyLength += 5 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
        bodyLength += 5;

        return bodyLength;
    }

    protected int computeBodyLength(int msgSeqNum, long time, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + IntFormatter.uintLength(msgSeqNum);
        bodyLength += 4 + sessionID.senderCompId().length();
        if (sessionID.senderSubId() != null)
            bodyLength += 4 + sessionID.senderSubId().length();

        bodyLength += 4 + sessionID.targetCompId().length();
        if (sessionID.targetSubId() != null)
            bodyLength += 4 + sessionID.targetSubId().length();

        bodyLength += 4 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
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
            throw new InsufficientSpaceException(String.format("Message length %s exceeds buffer size %s", length, capacity));
    }

}
