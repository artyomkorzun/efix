package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.message.FieldUtil;
import org.efix.message.field.Tag;
import org.efix.util.ByteSequence;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.format.ByteFormatter;
import org.efix.util.format.IntFormatter;
import org.efix.util.format.TimestampFormatter;
import org.efix.util.type.TimestampType;

import static org.efix.util.format.BooleanFormatter.formatBoolean;


public class MessagePacker {

    protected final ByteSequence beginString;
    protected final SessionId sessionId;
    protected final MutableBuffer buffer;

    public MessagePacker(FixVersion FixVersion, SessionId sessionId, MutableBuffer buffer) {
        this.beginString = FixVersion.beginString();
        this.sessionId = sessionId;
        this.buffer = buffer;
    }

    public int pack(int msgSeqNum, long time, ByteSequence msgType,
                    Buffer body, int bodyOffset, int length) {

        int bodyLength = bodyLength(msgSeqNum, time, msgType, length);
        int messageLength = messageLength(bodyLength);

        int offset = 0;
        offset = addStandardHeader(bodyLength, msgSeqNum, time, msgType, buffer, offset);
        offset = addBody(body, bodyOffset, length, buffer, offset);
        offset = addCheckSum(buffer, offset);

        return messageLength;
    }

    public int pack(int msgSeqNum, long time, long origTime, ByteSequence msgType,
                    Buffer body, int bodyOffset, int length) {

        int bodyLength = bodyLength(msgSeqNum, time, origTime, msgType, length);
        int messageLength = messageLength(bodyLength);

        int offset = 0;
        offset = addStandardHeader(bodyLength, msgSeqNum, time, msgType, buffer, offset);
        offset = addTimestamp(Tag.OrigSendingTime, origTime, buffer, offset);
        offset = addBoolean(Tag.PossDupFlag, true, buffer, offset);
        offset = addBody(body, bodyOffset, length, buffer, offset);
        offset = addCheckSum(buffer, offset);

        return messageLength;
    }

    protected int addStandardHeader(int bodyLength, int msgSeqNum, long time, ByteSequence msgType,
                                    MutableBuffer buffer, int offset) {

        offset = addByteSequence(Tag.BeginString, beginString, buffer, offset);
        offset = addUInt(Tag.BodyLength, bodyLength, buffer, offset);
        offset = addByteSequence(Tag.MsgType, msgType, buffer, offset);
        offset = addUInt(Tag.MsgSeqNum, msgSeqNum, buffer, offset);
        offset = addByteSequence(Tag.SenderCompID, sessionId.senderCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.SenderSubID, sessionId.senderSubId(), buffer, offset);
        offset = addByteSequence(Tag.TargetCompID, sessionId.targetCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.TargetSubID, sessionId.targetSubId(), buffer, offset);
        offset = addTimestamp(Tag.SendingTime, time, buffer, offset);

        return offset;
    }

    protected static int addBody(Buffer body, int bodyOffset, int length, MutableBuffer buffer, int offset) {
        return ByteFormatter.formatBytes(body, bodyOffset, length, buffer, offset);
    }

    protected static int addCheckSum(MutableBuffer buffer, int offset) {
        int checkSum = checkSum(buffer, 0, offset);

        offset = IntFormatter.formatUInt(Tag.CheckSum, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset);
        offset = IntFormatter.format3DigitUInt(checkSum, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addNullableByteSequence(int tag, ByteSequence value, MutableBuffer buffer, int offset) {
        return value == null ? offset : addByteSequence(tag, value, buffer, offset);
    }

    protected static int addBoolean(int tag, boolean value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.formatUInt(tag, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBoolean(value, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addByteSequence(int tag, ByteSequence value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.formatUInt(tag, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset);
        offset = ByteFormatter.formatBytes(value.buffer(), 0, value.length(), buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addUInt(int tag, int value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.formatUInt(tag, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset);
        offset = IntFormatter.formatUInt(value, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addTimestamp(int tag, long value, MutableBuffer buffer, int offset) {
        offset = IntFormatter.formatUInt(tag, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset);
        offset = TimestampFormatter.formatTimestamp(value, buffer, offset);
        offset = ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected int bodyLength(int msgSeqNum, long time, ByteSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + IntFormatter.uintLength(msgSeqNum);
        bodyLength += 4 + sessionId.senderCompId().length();
        bodyLength += (sessionId.senderSubId() == null) ? 0 : 4 + sessionId.senderSubId().length();
        bodyLength += 4 + sessionId.targetCompId().length();
        bodyLength += (sessionId.targetSubId() == null) ? 0 : 4 + sessionId.targetSubId().length();
        bodyLength += 4 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
        bodyLength += length;

        return bodyLength;
    }

    protected int bodyLength(int msgSeqNum, long time, long origTime, ByteSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += bodyLength(msgSeqNum, time, msgType, length);
        bodyLength += 5 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
        bodyLength += 5;

        return bodyLength;
    }

    protected int messageLength(int bodyLength) {
        int length = 0;

        length += 3 + beginString.length();
        length += 3 + IntFormatter.uintLength(bodyLength);
        length += bodyLength;
        length += FieldUtil.CHECK_SUM_FIELD_LENGTH;

        int capacity = buffer.capacity();
        if (length > capacity)
            throw new InsufficientSpaceException(String.format("Message length %s exceeds buffer size %s", length, capacity));

        return length;
    }

    protected static int checkSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return sum & 0xFF;
    }

}
