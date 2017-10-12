package org.efix.message.builder;

import org.efix.util.ByteSequence;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.format.DateFormatter;
import org.efix.util.format.TimestampFormatter;

import static org.efix.message.FieldUtil.FIELD_SEPARATOR;
import static org.efix.message.FieldUtil.TAG_VALUE_SEPARATOR;
import static org.efix.util.format.BooleanFormatter.formatBoolean;
import static org.efix.util.format.ByteFormatter.formatByte;
import static org.efix.util.format.ByteFormatter.formatBytes;
import static org.efix.util.format.CharFormatter.formatChar;
import static org.efix.util.format.CharFormatter.formatChars;
import static org.efix.util.format.DecimalFormatter.formatDecimal;
import static org.efix.util.format.DoubleFormatter.formatDouble;
import static org.efix.util.format.IntFormatter.formatInt;
import static org.efix.util.format.IntFormatter.formatUInt;
import static org.efix.util.format.LongFormatter.formatLong;
import static org.efix.util.format.TimeFormatter.formatTimeMs;


public class FastMessageBuilder implements MessageBuilder {

    protected MutableBuffer buffer;
    protected int start;
    protected int end;
    protected int offset;

    @Override
    public MessageBuilder addBoolean(int tag, boolean value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBoolean(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatByte(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatChar(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatInt(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatLong(value, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatDouble(value, precision, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatDouble(value, precision, roundUp, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addDecimal(int tag, long value, int scale) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatDecimal(value, scale, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addTimestampMs(int tag, long timestampMs) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = TimestampFormatter.formatTimestampMs(timestampMs, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addTimestampNs(int tag, long timestampNs) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = TimestampFormatter.formatTimestampNs(timestampNs, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatTimeMs(timestamp, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = DateFormatter.formatDate(timestamp, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value, 0, value.length, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int valueOffset, int length) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value, valueOffset, length, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value, 0, value.capacity(), buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int valueOffset, int length) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value, valueOffset, length, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value.buffer(), 0, value.length(), buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int valueOffset, int length) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatBytes(value.buffer(), valueOffset, length, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatChars(value, 0, value.length(), buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int valueOffset, int length) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        offset = formatChars(value, valueOffset, length, buffer, offset);
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder startField(int tag) {
        offset = formatUInt(tag, buffer, offset);
        offset = formatByte(TAG_VALUE_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder endField() {
        offset = formatByte(FIELD_SEPARATOR, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        offset = formatBoolean(value, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        offset = formatByte(value, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendChar(char value) {
        offset = formatChar(value, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendInt(int value) {
        offset = formatInt(value, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendLong(long value) {
        offset = formatLong(value, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        offset = formatDouble(value, precision, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        offset = formatDouble(value, precision, roundHalfUp, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendDecimal(long value, int scale) {
        offset = formatDecimal(value, scale, buffer, offset());
        return this;
    }

    @Override
    public MessageBuilder appendTimestampMs(long timestampMs) {
        offset = TimestampFormatter.formatTimestampMs(timestampMs, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendTimestampNs(final long timestampNs) {
        offset = TimestampFormatter.formatTimestampNs(timestampNs, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        offset = formatTimeMs(timestamp, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        offset = DateFormatter.formatDate(timestamp, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value) {
        offset = formatBytes(value, 0, value.length, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int valueOffset, int length) {
        offset = formatBytes(value, valueOffset, length, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        return appendBytes(value, 0, value.capacity());
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int valueOffset, int length) {
        offset = formatBytes(value, valueOffset, length, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        offset = formatBytes(value.buffer(), 0, value.length(), buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int valueOffset, int length) {
        offset = formatBytes(value.buffer(), valueOffset, length, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        return appendCharSequence(value, 0, value.length());
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int valueOffset, int length) {
        offset = formatChars(value, valueOffset, length, buffer, offset);
        return this;
    }

    @Override
    public MessageBuilder reset() {
        offset = start;
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer) {
        this.buffer = buffer;
        this.offset = 0;
        this.start = 0;
        this.end = buffer.capacity();
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.start = offset;
        this.end = offset + length;
        return this;
    }

    @Override
    public MutableBuffer buffer() {
        return buffer;
    }

    @Override
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public MessageBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public int length() {
        return offset - start;
    }

    @Override
    public int remaining() {
        return end - offset;
    }

}
