package org.f1x.message.builder;

import org.f1x.message.FieldUtil;
import org.f1x.util.ByteSequence;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.format.*;


public class FastMessageBuilder implements MessageBuilder {

    protected final MutableInt offset = new MutableInt();

    protected MutableBuffer buffer;
    protected int start;
    protected int end;

    @Override
    public MessageBuilder addBoolean(int tag, boolean value) {
        return startField(tag).appendBoolean(value).endField();
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        return startField(tag).appendByte(value).endField();
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        return startField(tag).appendChar(value).endField();
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        return startField(tag).appendInt(value).endField();
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        return startField(tag).appendLong(value).endField();
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        return startField(tag).appendDouble(value, precision).endField();
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        return startField(tag).appendDouble(value, precision, roundUp).endField();
    }

    @Override
    public MessageBuilder addTimestamp(int tag, long timestamp) {
        return startField(tag).appendTimestamp(timestamp).endField();
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        return startField(tag).appendTime(timestamp).endField();
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        return startField(tag).appendDate(timestamp).endField();
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        return startField(tag).appendBytes(value).endField();
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int offset, int length) {
        return startField(tag).appendBytes(value, offset, length).endField();
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        return startField(tag).appendBytes(value).endField();
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int offset, int length) {
        return startField(tag).appendBytes(value, offset, length).endField();
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        return startField(tag).appendByteSequence(value).endField();
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length) {
        return startField(tag).appendByteSequence(value, offset, length).endField();
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        return startField(tag).appendCharSequence(value).endField();
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length) {
        return startField(tag).appendCharSequence(value, offset, length).endField();
    }

    @Override
    public MessageBuilder startField(int tag) {
        appendInt(tag);
        return appendByte(FieldUtil.TAG_VALUE_SEPARATOR);
    }

    @Override
    public MessageBuilder endField() {
        return appendByte(FieldUtil.FIELD_SEPARATOR);
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        BooleanFormatter.formatBoolean(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        ByteFormatter.formatByte(value, buffer, offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendChar(char value) {
        CharFormatter.formatChar(value, buffer, offset, end);
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
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        DoubleFormatter.formatDouble(value, precision, roundHalfUp, buffer, offset, end);
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
    public MessageBuilder appendBytes(byte[] value) {
        return appendBytes(value, 0, value.length);
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        ByteFormatter.formatBytes(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        return appendBytes(value, 0, value.capacity());
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        ByteFormatter.formatBytes(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        return appendByteSequence(value, 0, value.length());
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        return appendBytes(value.wrapper(), offset, length);
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        return appendCharSequence(value, 0, value.length());
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        CharFormatter.formatChars(value, offset, length, buffer, this.offset, end);
        return this;
    }

    @Override
    public MessageBuilder reset() {
        offset.value(start);
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer) {
        return wrap(buffer, 0, buffer.capacity());
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset.value(offset);
        this.start = offset;
        this.end = offset + length;
        return this;
    }

    @Override
    public int offset() {
        return offset.value();
    }

    @Override
    public int length() {
        return offset.value() - start;
    }

    @Override
    public int remaining() {
        return end - offset.value();
    }

}
