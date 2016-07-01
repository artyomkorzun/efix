package org.efix.message.builder;

import org.efix.util.ByteSequence;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.efix.util.type.*;

/**
 * Checks bounds on add/append. Doesn't check bounds on wrap.
 */
public class CheckedMessageBuilder implements MessageBuilder {

    protected static final boolean CHECK_BOUNDS = !UnsafeBuffer.CHECK_BOUNDS;

    protected final MessageBuilder builder;

    protected CheckedMessageBuilder(MessageBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MessageBuilder addBoolean(int tag, boolean value) {
        checkBounds(tag, BooleanType.LENGTH);
        builder.addBoolean(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        checkBounds(tag, ByteType.LENGTH);
        builder.addByte(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        checkBounds(tag, CharType.LENGTH);
        builder.addChar(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        checkBounds(tag, IntType.MAX_LENGTH);
        builder.addInt(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        checkBounds(tag, LongType.MAX_LENGTH);
        builder.addLong(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        checkBounds(tag, DoubleType.MAX_LENGTH);
        builder.addDouble(tag, value, precision);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        checkBounds(tag, DoubleType.MAX_LENGTH);
        builder.addDouble(tag, value, precision, roundUp);
        return this;
    }

    @Override
    public MessageBuilder addDecimal(int tag, long value, int scale) {
        checkBounds(tag, DecimalType.MAX_LENGTH);
        builder.addDecimal(tag, value, scale);
        return this;
    }

    @Override
    public MessageBuilder addTimestamp(int tag, long timestamp) {
        checkBounds(tag, TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        builder.addTimestamp(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        checkBounds(tag, TimeType.MILLISECOND_TIME_LENGTH);
        builder.addTime(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        checkBounds(tag, DateType.LENGTH);
        builder.addDate(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        checkBounds(tag, value.length);
        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int offset, int length) {
        checkBounds(tag, length);
        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        checkBounds(tag, value.capacity());
        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int offset, int length) {
        checkBounds(tag, length);
        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        checkBounds(tag, value.length());
        builder.addByteSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length) {
        checkBounds(tag, length);
        builder.addByteSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        checkBounds(tag, value.length());
        builder.addCharSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length) {
        checkBounds(tag, length);
        builder.addCharSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder startField(int tag) {
        checkBounds(IntType.MAX_UINT_LENGTH + ByteType.LENGTH);
        builder.startField(tag);
        return this;
    }

    @Override
    public MessageBuilder endField() {
        checkBounds(ByteType.LENGTH);
        builder.endField();
        return this;
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        checkBounds(BooleanType.LENGTH);
        builder.appendBoolean(value);
        return this;
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        checkBounds(ByteType.LENGTH);
        builder.appendByte(value);
        return this;
    }

    @Override
    public MessageBuilder appendChar(char value) {
        checkBounds(CharType.LENGTH);
        builder.appendChar(value);
        return this;
    }

    @Override
    public MessageBuilder appendInt(int value) {
        checkBounds(IntType.MAX_LENGTH);
        builder.appendInt(value);
        return this;
    }

    @Override
    public MessageBuilder appendLong(long value) {
        checkBounds(LongType.MAX_LENGTH);
        builder.appendLong(value);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        checkBounds(DoubleType.MAX_LENGTH);
        builder.appendDouble(value, precision);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        checkBounds(DoubleType.MAX_LENGTH);
        builder.appendDouble(value, precision, roundHalfUp);
        return this;
    }

    @Override
    public MessageBuilder appendDecimal(long value, int scale) {
        checkBounds(DecimalType.MAX_LENGTH);
        builder.appendDecimal(value, scale);
        return this;
    }

    @Override
    public MessageBuilder appendTimestamp(long timestamp) {
        checkBounds(TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        builder.appendTimestamp(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        checkBounds(TimeType.MILLISECOND_TIME_LENGTH);
        builder.appendTime(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        checkBounds(DateType.LENGTH);
        builder.appendDate(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value) {
        checkBounds(value.length);
        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        checkBounds(length);
        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        checkBounds(value.capacity());
        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        checkBounds(length);
        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        checkBounds(value.length());
        builder.appendByteSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        checkBounds(length);
        builder.appendByteSequence(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        checkBounds(value.length());
        builder.appendCharSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        checkBounds(length);
        builder.appendCharSequence(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder reset() {
        builder.reset();
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer) {
        builder.wrap(buffer);
        return this;
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        builder.wrap(buffer, offset, length);
        return this;
    }

    @Override
    public MutableBuffer buffer() {
        return builder.buffer();
    }

    @Override
    public int start() {
        return builder.start();
    }

    @Override
    public int end() {
        return builder.end();
    }

    @Override
    public MessageBuilder offset(int offset) {
        builder.offset(offset);
        return this;
    }

    @Override
    public int offset() {
        return builder.offset();
    }

    @Override
    public int length() {
        return builder.length();
    }

    @Override
    public int remaining() {
        return builder.remaining();
    }

    public static CheckedMessageBuilder wrap(MessageBuilder builder) {
        return new CheckedMessageBuilder(builder);
    }

    private void checkBounds(int tag, int valueLength) {
        checkBounds(IntType.MAX_UINT_LENGTH + ByteType.LENGTH + valueLength + ByteType.LENGTH);
    }

    private void checkBounds(int required) {
        if (CHECK_BOUNDS) {
            int remaining = remaining();
            if (required > remaining)
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
        }
    }

}
