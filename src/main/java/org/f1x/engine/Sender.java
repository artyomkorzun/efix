package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.builder.ByteBufferMessageBuilder;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.FixTags;
import org.f1x.util.Fields;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.format.IntFormatter;
import org.f1x.util.format.TimestampFormatter;

public class Sender {

    protected final static int MIN_MSG_TYPE_END_OFFSET = Fields.tagWithSeparatorLength(FixTags.MsgType) + 1; // minimum one character msg type

    protected final MessageLog log;

    protected final ByteBufferMessageBuilder builder = new ByteBufferMessageBuilder(1);
    protected final Buffer beginStringWithBodyLengthChunk;
    protected final Buffer msgSeqNumChunk;
    protected final Buffer sessionIDWithSendingTimeChunk;
    protected final Buffer checkSumChunk;

    protected final int precomputedBodyLength;
    protected final int precomputedMessageLength;
    protected final int precomputedCheckSum;

    protected final MutableBuffer buffer = null;

    protected Channel channel;

    public Sender(FIXVersion version, SessionID sessionID, MessageLog log) {
        this.log = log;

        beginStringWithBodyLengthChunk = createBeginWithBodyLengthChunk(version.getBeginString(), builder);
        msgSeqNumChunk = createFieldChunk(FixTags.MsgSeqNum, builder);
        sessionIDWithSendingTimeChunk = createSessionIDWithSendingTimeChunk(sessionID, builder);
        checkSumChunk = createFieldChunk(FixTags.CheckSum, builder);
        precomputedBodyLength = precomputeBodyLength(msgSeqNumChunk, sessionIDWithSendingTimeChunk);
        precomputedMessageLength = precomputeMessageLength(beginStringWithBodyLengthChunk);
        precomputedCheckSum = precomputeCheckSum(beginStringWithBodyLengthChunk, msgSeqNumChunk, sessionIDWithSendingTimeChunk);

    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void send(int msgSeqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        Channel channel = this.channel;
        if (channel == null)
            return;

        int bodyLength = computeBodyLength(msgSeqNum, length);
        int messageLength = computeMessageLength(bodyLength);

        ByteBufferMessageBuilder builder = this.builder.wrap(buffer);

        int sum = precomputedCheckSum;
        sum += appendIntFieldChunk(bodyLength, beginStringWithBodyLengthChunk, builder);

        int nextFieldOffset = findNextFieldOffsetAfterMsgType(chunkBuffer, chunkOffset, chunkLength);
        int msgTypeFieldLength = nextFieldOffset - chunkOffset;
        builder.append(chunkBuffer, chunkOffset, msgTypeFieldLength);

        sum += appendIntFieldChunk(msgSeqNum, msgSeqNumChunk, builder);
        sum += appendTimestampFieldChunk(sendingTime, sessionIDWithSendingTimeChunk, builder);

        builder.append(chunkBuffer, nextFieldOffset, chunkLength - msgTypeFieldLength);
        sum += computeSum(chunkBuffer, chunkOffset, chunkLength);

        builder.append(checkSumChunk).append3Digit(Fields.checkSum(sum)).end();
    }

    public void send(int msgSeqNum, long sendingTime, boolean possDup, long origSendingTime, CharSequence msgType, Buffer body, int offset, int length) {

    }

    protected int computeBodyLength(int msgSeqNum, int chunkLength) {
        return precomputedBodyLength + IntFormatter.stringSize(msgSeqNum) + chunkLength;
    }

    protected int computeMessageLength(int bodyLength) {
        return precomputedMessageLength + bodyLength + IntFormatter.stringSize(bodyLength);
    }

    protected static int precomputeBodyLength(Buffer msgSeqNumChunk, Buffer sessionIDWithSendingTimeChunk) {
        return msgSeqNumChunk.capacity() + sessionIDWithSendingTimeChunk.capacity() + TimestampFormatter.DATE_TIME_LENGTH + 2 * Fields.FIELD_SEPARATOR_LENGTH;
    }

    private static int precomputeMessageLength(Buffer beginStringWithBodyLength) {
        return beginStringWithBodyLength.capacity() + Fields.FIELD_SEPARATOR_LENGTH + Fields.CHECK_SUM_FIELD_LENGTH;
    }

    private static int precomputeCheckSum(Buffer beginStringWithBodyLengthChunk, Buffer msgSeqNumChunk, Buffer sessionIDWithSendingTimeChunk) {
        int sum = computeSum(beginStringWithBodyLengthChunk) + computeSum(msgSeqNumChunk) + computeSum(sessionIDWithSendingTimeChunk) + 3 * Fields.FIELD_SEPARATOR_CHECK_SUM;
        return Fields.checkSum(sum);
    }

    protected static int computeSum(Buffer buffer) {
        int sum = 0;
        for (int i = 0, end = buffer.capacity(); i < end; i++)
            sum += buffer.getByte(i);

        return sum;
    }

    protected static int computeSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return sum;
    }

    protected static int findNextFieldOffsetAfterMsgType(Buffer buffer, int offset, int length) {
        for (int i = offset + MIN_MSG_TYPE_END_OFFSET, end = offset + length; i < end; i++) {
            if (buffer.getByte(i) == Fields.FIELD_SEPARATOR)
                return i + 1;
        }

        throw new IllegalArgumentException("Buffer does not contain SOH");
    }

    protected static int appendIntFieldChunk(int value, Buffer chunk, ByteBufferMessageBuilder builder) {
        builder.append(chunk);
        int start = builder.getOffset();
        builder.append(value);
        int end = builder.getOffset();
        builder.end();
        return computeSum(builder.getBuffer(), start, end - start);
    }

    protected static int appendTimestampFieldChunk(long value, Buffer chunk, ByteBufferMessageBuilder builder) {
        builder.append(chunk);
        int start = builder.getOffset();
        builder.appendTimestamp(value);
        int end = builder.getOffset();
        builder.end();
        return computeSum(builder.getBuffer(), start, end - start);
    }

    protected static Buffer createBeginWithBodyLengthChunk(CharSequence beginString, MessageBuilder builder) {
        int length = Fields.fieldLength(FixTags.BeginString, beginString) + Fields.tagWithSeparatorLength(FixTags.BodyLength);
        MutableBuffer buffer = new UnsafeBuffer(new byte[length]);

        builder.wrap(buffer);
        builder.add(FixTags.BeginString, beginString);
        builder.add(FixTags.BodyLength);

        return buffer;
    }

    protected static Buffer createFieldChunk(int field, MessageBuilder builder) {
        int length = Fields.tagWithSeparatorLength(field);
        MutableBuffer buffer = new UnsafeBuffer(new byte[length]);

        builder.wrap(buffer);
        builder.add(field);

        return buffer;
    }

    protected static Buffer createSessionIDWithSendingTimeChunk(SessionID sessionID, MessageBuilder builder) {
        CharSequence senderCompID = sessionID.getSenderCompId();
        CharSequence senderSubID = sessionID.getSenderSubId();
        CharSequence targetCompID = sessionID.getTargetCompId();
        CharSequence targetSubID = sessionID.getTargetSubId();

        int length = Fields.fieldLength(FixTags.SenderCompID, senderCompID) + Fields.nullableFieldLength(FixTags.SenderSubID, senderSubID) +
                Fields.fieldLength(FixTags.TargetCompID, targetCompID) + Fields.nullableFieldLength(FixTags.TargetSubID, targetSubID) +
                Fields.tagWithSeparatorLength(FixTags.SendingTime);

        MutableBuffer buffer = new UnsafeBuffer(new byte[length]);
        builder.wrap(buffer);

        builder.add(FixTags.SenderCompID, senderCompID);
        if (senderSubID != null)
            builder.add(FixTags.SenderSubID, senderSubID);

        builder.add(FixTags.TargetCompID, targetCompID);
        if (targetSubID != null)
            builder.add(FixTags.TargetSubID, targetSubID);

        builder.add(FixTags.SendingTime);

        return buffer;
    }

}
