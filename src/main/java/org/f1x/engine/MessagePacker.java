package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.message.Fields;
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
        builder.append(body, offset, length);
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
        builder.add(FixTags.PossDupFlag, true);
        builder.addUTCTimestamp(FixTags.OrigSendingTime, origSendingTime);
        builder.append(body, offset, length);
        addCheckSum(builder);

        wrapper.wrap(buffer, 0, builder.length());
        return wrapper;
    }

    protected void addStandardHeader(int bodyLength, int msgSeqNum, long sendingTime, CharSequence msgType, MessageBuilder builder) {
        builder.add(FixTags.BeginString, beginString);
        builder.add(FixTags.BodyLength, bodyLength);
        builder.add(FixTags.MsgType, msgType);
        builder.add(FixTags.MsgSeqNum, msgSeqNum);

        builder.add(FixTags.SenderCompID, sessionID.getSenderCompId());
        if (sessionID.getSenderSubId() != null)
            builder.add(FixTags.SenderSubID, sessionID.getSenderSubId());

        builder.add(FixTags.TargetCompID, sessionID.getTargetCompId());
        if (sessionID.getTargetSubId() != null)
            builder.add(FixTags.TargetSubID, sessionID.getTargetSubId());

        builder.addUTCTimestamp(FixTags.SendingTime, sendingTime);
    }

    protected void addCheckSum(MessageBuilder builder) {
        int checkSum = computeCheckSum(buffer, 0, builder.length());
        builder.append(FixTags.CheckSum).append(checkSum, 3);
    }

    protected int computeBodyLength(int msgSeqNum, long sendingTime, long origSendingTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += computeBodyLength(msgSeqNum, sendingTime, msgType, length);
        bodyLength += 5;
        bodyLength += 4 + TimestampFormatter.DATE_TIME_LENGTH;

        return bodyLength;
    }

    protected int computeBodyLength(int msgSeqNum, long sendingTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + IntFormatter.stringSize(msgSeqNum);
        bodyLength += 4 + sessionID.getSenderCompId().length();
        if (sessionID.getSenderSubId() != null)
            bodyLength += 4 + sessionID.getSenderSubId().length();

        bodyLength += 4 + sessionID.getTargetCompId().length();
        if (sessionID.getTargetSubId() != null)
            bodyLength += 4 + sessionID.getTargetSubId().length();

        bodyLength += 4 + TimestampFormatter.DATE_TIME_LENGTH;
        bodyLength += length;

        return bodyLength;
    }

    protected int computeMessageLength(int bodyLength) {
        return 3 + beginString.length() +
                3 + IntFormatter.stringSize(bodyLength) +
                bodyLength +
                Fields.CHECK_SUM_FIELD_LENGTH;
    }

    protected int computeCheckSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return Fields.checkSum(sum);
    }

    protected void checkMessageLength(int length) {
        int capacity = buffer.capacity();
        if (length > capacity)
            throw new IllegalArgumentException(String.format("Message length %s exceeds buffer capacity %s", length, capacity));
    }

}
