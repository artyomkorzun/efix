package org.f1x.message.builder.newone;

import org.f1x.message.FieldUtil;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.format.newone.*;

/**
 * Checks buffer bounds. Doesn't check arguments (non positive tag, char sequence with SOH/not ASCII character and etc)
 */
public class FastMessageBuilder implements MessageBuilder {

    protected final MutableInt offset = new MutableInt();

    protected MutableBuffer buffer;
    protected int start;
    protected int end;

    @Override
    public MessageBuilder startField(int tag) {
        IntFormatter.formatUInt(tag, buffer, offset, end);
        ByteFormatter.formatByte(FieldUtil.TAG_VALUE_SEPARATOR, buffer, offset, end);
        return this;
    }

    @Override
    public void endField() {
        ByteFormatter.formatByte(FieldUtil.FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        ByteFormatter.formatByte(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        BooleanFormatter.formatBoolean(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendInt(int value) {
        IntFormatter.formatInt(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendLong(long value) {
        LongFormatter.formatLong(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        DoubleFormatter.formatDouble(value, precision, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundUp) {
        DoubleFormatter.formatDouble(value, precision, roundUp, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendTimestamp(long timestamp) {
        TimestampFormatter.formatTimestamp(timestamp, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        TimeFormatter.formatTime(timestamp, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        DateFormatter.formatDate(timestamp, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        ByteFormatter.formatBytes(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        ByteFormatter.formatBytes(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        CharFormatter.formatChars(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public int length() {
        return offset.value() - start;
    }

    @Override
    public MessageBuilder reset() {
        offset.value(start);
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset.value(offset);
        this.start = offset;
        this.end = offset + length;
        return this;
    }

}
