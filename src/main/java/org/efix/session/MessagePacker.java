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

    protected final MutableBuffer buffer;
    protected final boolean withLastMsgSeqNumProcessed;

    public MessagePacker(MutableBuffer buffer) {
        this(buffer, false);
    }

    public MessagePacker(MutableBuffer buffer, boolean withLastMsgSeqNumProcessed) {
        this.buffer = buffer;
        this.withLastMsgSeqNumProcessed = withLastMsgSeqNumProcessed;
    }

    public int pack(FixVersion fixVersion, SessionId sessionId, int msgSeqNum, int lastMsgSeqNumProcessed, long time, ByteSequence msgType,
                    Buffer body, int bodyOffset, int length) {

        int bodyLength = bodyLength(sessionId, msgSeqNum, lastMsgSeqNumProcessed, time, msgType, length);
        int messageLength = messageLength(fixVersion, bodyLength);

        int offset = 0;
        offset = addStandardHeader(fixVersion, sessionId, bodyLength, msgSeqNum, lastMsgSeqNumProcessed, time, msgType, buffer, offset);
        offset = addBody(body, bodyOffset, length, buffer, offset);
        offset = addCheckSum(buffer, offset);

        return messageLength;
    }

    public int pack(FixVersion fixVersion, SessionId sessionId, int msgSeqNum, int lastMsgSeqNumProcessed, long time, long origTime, ByteSequence msgType,
                    Buffer body, int bodyOffset, int length) {

        int bodyLength = bodyLength(sessionId, msgSeqNum, lastMsgSeqNumProcessed, time, origTime, msgType, length);
        int messageLength = messageLength(fixVersion, bodyLength);

        int offset = 0;
        offset = addStandardHeader(fixVersion, sessionId, bodyLength, msgSeqNum, lastMsgSeqNumProcessed, time, msgType, buffer, offset);
        offset = addTimestamp(Tag.OrigSendingTime, origTime, buffer, offset);
        offset = addBoolean(Tag.PossDupFlag, true, buffer, offset);
        offset = addBody(body, bodyOffset, length, buffer, offset);
        offset = addCheckSum(buffer, offset);

        return messageLength;
    }

    protected int addStandardHeader(FixVersion version, SessionId sessionId, int bodyLength, int msgSeqNum, int lastMsgSeqNumProcessed, long time, ByteSequence msgType,
                                    MutableBuffer buffer, int offset) {

        offset = addByteSequence(Tag.BeginString, version.beginString(), buffer, offset);
        offset = addUInt(Tag.BodyLength, bodyLength, buffer, offset);
        offset = addByteSequence(Tag.MsgType, msgType, buffer, offset);
        offset = addUInt(Tag.MsgSeqNum, msgSeqNum, buffer, offset);
        if (withLastMsgSeqNumProcessed) {
            offset = addUInt(Tag.LastMsgSeqNumProcessed, lastMsgSeqNumProcessed, buffer, offset);
        }
        offset = addByteSequence(Tag.SenderCompID, sessionId.senderCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.SenderSubID, sessionId.senderSubId(), buffer, offset);
        offset = addByteSequence(Tag.TargetCompID, sessionId.targetCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.TargetSubID, sessionId.targetSubId(), buffer, offset);
        offset = addTimestamp(Tag.SendingTime, time, buffer, offset);
        offset = addNullableByteSequence(Tag.SenderLocationID, sessionId.senderLocationId(), buffer, offset);

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

    protected int bodyLength(SessionId sessionId, int msgSeqNum, int lastMsgSeqNumProcessed, long time, ByteSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + IntFormatter.uintLength(msgSeqNum);
        if (withLastMsgSeqNumProcessed) {
            bodyLength += 5 + IntFormatter.uintLength(lastMsgSeqNumProcessed);
        }
        bodyLength += 4 + sessionId.senderCompId().length();
        bodyLength += (sessionId.senderSubId() == null) ? 0 : 4 + sessionId.senderSubId().length();
        bodyLength += 4 + sessionId.targetCompId().length();
        bodyLength += (sessionId.targetSubId() == null) ? 0 : 4 + sessionId.targetSubId().length();
        bodyLength += 4 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
        bodyLength += (sessionId.senderLocationId() == null) ? 0 : 5 + sessionId.senderLocationId().length();
        bodyLength += length;

        return bodyLength;
    }

    protected int bodyLength(SessionId sessionId, int msgSeqNum, int lastMsgSeqNumProcessed, long time, long origTime, ByteSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += bodyLength(sessionId, msgSeqNum, lastMsgSeqNumProcessed, time, msgType, length);
        bodyLength += 5 + TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
        bodyLength += 5;

        return bodyLength;
    }

    protected int messageLength(FixVersion fixVersion, int bodyLength) {
        int length = 0;

        length += 3 + fixVersion.beginString().length();
        length += 3 + IntFormatter.uintLength(bodyLength);
        length += bodyLength;
        length += FieldUtil.CHECK_SUM_FIELD_LENGTH;

        int capacity = buffer.capacity();
        if (length > capacity) {
            throw new InsufficientSpaceException(String.format("Message length %s exceeds buffer size %s", length, capacity));
        }

        return length;
    }

    protected static int checkSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return sum & 0xFF;
    }

}
