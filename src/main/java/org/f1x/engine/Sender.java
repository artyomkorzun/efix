package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionID;
import org.f1x.connector.channel.Channel;
import org.f1x.log.MessageLog;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.FixTags;
import org.f1x.util.Fields;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.format.IntFormatter;
import org.f1x.util.format.TimestampFormatter;

public class Sender {

    protected final FIXVersion version;
    protected final SessionID sessionID;
    protected final MessageLog log;
    protected final MessageBuilder builder;
    protected final MutableBuffer buffer;

    protected Channel channel;

    public Sender(FIXVersion version, SessionID sessionID, MessageLog log, MessageBuilder builder, MutableBuffer buffer) {
        this.version = version;
        this.sessionID = sessionID;
        this.log = log;
        this.builder = builder.wrap(buffer);
        this.buffer = buffer;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void send(int msgSeqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        MessageBuilder builder = this.builder.clear();
        int bodyLength = computeBodyLength(msgSeqNum, sendingTime, msgType, length);
        addStandardHeader(bodyLength, msgSeqNum, sendingTime, msgType, builder);
        builder.append(body, offset, length);
        addCheckSum(builder);
        send(buffer, builder.length());
    }


    public void send(boolean possDup, int msgSeqNum, long sendingTime, long origSendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        MessageBuilder builder = this.builder.clear();
        int bodyLength = computeBodyLength(possDup, msgSeqNum, sendingTime, origSendingTime, msgType, length);
        addStandardHeader(bodyLength, msgSeqNum, sendingTime, msgType, builder);
        builder.add(FixTags.PossDupFlag, possDup);
        builder.addUTCTimestamp(FixTags.OrigSendingTime, origSendingTime);
        builder.append(body, offset, length);
        addCheckSum(builder);
        send(buffer, builder.length());
    }

    protected void send(Buffer buffer, int length) {
        try {
            int written = 0;
            while ((written += channel.write(buffer, written, length - written)) < length)
                Thread.yield();

        } finally {
            log.log(false, buffer, 0, length);
        }
    }

    protected void addStandardHeader(int bodyLength, int msgSeqNum, long sendingTime, CharSequence msgType, MessageBuilder builder) {
        builder.add(FixTags.BeginString, version.getBeginString());
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
        builder.append(FixTags.CheckSum).append3Digit(checkSum);
    }

    protected int computeBodyLength(boolean possDup, int msgSeqNum, long sendingTime, long origSendingTime, CharSequence msgType, int length) {
        int bodyLength = 0;

        bodyLength += computeBodyLength(msgSeqNum, sendingTime, msgType, length);
        bodyLength += 5; // 43=Y|
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

    protected int computeCheckSum(Buffer buffer, int offset, int length) {
        int sum = 0;
        for (int i = offset, end = offset + length; i < end; i++)
            sum += buffer.getByte(i);

        return Fields.checkSum(sum);
    }

}
