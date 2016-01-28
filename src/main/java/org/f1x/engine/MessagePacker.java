package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.message.field.Tag;
import org.f1x.util.ByteSequence;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.type.TimestampType;

import static org.f1x.message.FieldUtil.*;
import static org.f1x.util.format.BooleanFormatter.formatBoolean;
import static org.f1x.util.format.ByteFormatter.formatByte;
import static org.f1x.util.format.ByteFormatter.formatBytes;
import static org.f1x.util.format.IntFormatter.*;
import static org.f1x.util.format.TimestampFormatter.formatTimestamp;


public class MessagePacker {

    protected final ByteSequence beginString;
    protected final SessionID sessionID;
    protected final MutableBuffer buffer;

    public MessagePacker(FIXVersion FIXVersion, SessionID sessionID, MutableBuffer buffer) {
        this.beginString = FIXVersion.beginString();
        this.sessionID = sessionID;
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
        offset = addByteSequence(Tag.SenderCompID, sessionID.senderCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.SenderSubID, sessionID.senderSubId(), buffer, offset);
        offset = addByteSequence(Tag.TargetCompID, sessionID.targetCompId(), buffer, offset);
        offset = addNullableByteSequence(Tag.TargetSubID, sessionID.targetSubId(), buffer, offset);
        offset = addTimestamp(Tag.SendingTime, time, buffer, offset);

        return offset;
    }

    protected static int addBody(Buffer body, int bodyOffset, int length, MutableBuffer buffer, int offset) {
        return formatBytes(body, bodyOffset, length, buffer, offset);
    }

    protected static int addCheckSum(MutableBuffer buffer, int offset) {
        int checkSum = checkSum(buffer, 0, offset);

        offset = formatUInt(Tag.CheckSum, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = format3DigitUInt(checkSum, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addNullableByteSequence(int tag, ByteSequence value, MutableBuffer buffer, int offset) {
        return value == null ? offset : addByteSequence(tag, value, buffer, offset);
    }

    protected static int addBoolean(int tag, boolean value, MutableBuffer buffer, int offset) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBoolean(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addByteSequence(int tag, ByteSequence value, MutableBuffer buffer, int offset) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value.buffer(), 0, value.length(), buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addUInt(int tag, int value, MutableBuffer buffer, int offset) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatUInt(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected static int addTimestamp(int tag, long value, MutableBuffer buffer, int offset) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatTimestamp(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);

        return offset;
    }

    protected int bodyLength(int msgSeqNum, long time, ByteSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += 4 + msgType.length();
        bodyLength += 4 + uintLength(msgSeqNum);
        bodyLength += 4 + sessionID.senderCompId().length();
        bodyLength += (sessionID.senderSubId() == null) ? 0 : 4 + sessionID.senderSubId().length();
        bodyLength += 4 + sessionID.targetCompId().length();
        bodyLength += (sessionID.targetSubId() == null) ? 0 : 4 + sessionID.targetSubId().length();
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
        length += 3 + uintLength(bodyLength);
        length += bodyLength;
        length += CHECK_SUM_FIELD_LENGTH;

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
