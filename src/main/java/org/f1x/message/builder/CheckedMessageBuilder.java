package org.f1x.message.builder;

import org.f1x.util.ByteSequence;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.type.*;

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
        checkSpaceForField(BooleanType.LENGTH);
        builder.addBoolean(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        checkSpaceForField(ByteType.LENGTH);
        builder.addByte(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        checkSpaceForField(CharType.LENGTH);
        builder.addChar(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        checkSpaceForField(IntType.MAX_LENGTH);
        builder.addInt(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        checkSpaceForField(LongType.MAX_LENGTH);
        builder.addLong(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        checkSpaceForField(DoubleType.MAX_LENGTH);
        builder.addDouble(tag, value, precision);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        checkSpaceForField(DoubleType.MAX_LENGTH);
        builder.addDouble(tag, value, precision, roundUp);
        return this;
    }

    @Override
    public MessageBuilder addTimestamp(int tag, long timestamp) {
        checkSpaceForField(TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        builder.addTimestamp(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        checkSpaceForField(TimeType.MILLISECOND_TIME_LENGTH);
        builder.addTime(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        checkSpaceForField(DateType.LENGTH);
        builder.addDate(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        checkSpaceForField(value.length);
        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int offset, int length) {
        checkSpaceForField(length);
        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        checkSpaceForField(value.capacity());
        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int offset, int length) {
        checkSpaceForField(length);
        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        checkSpaceForField(value.length());
        builder.addByteSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length) {
        checkSpaceForField(length);
        builder.addByteSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        checkSpaceForField(value.length());
        builder.addCharSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length) {
        checkSpaceForField(length);
        builder.addCharSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder startField(int tag) {
        checkSpace(IntType.MAX_UNSIGNED_INT_LENGTH + 1);
        builder.startField(tag);
        return this;
    }

    @Override
    public MessageBuilder endField() {
        checkSpace(1);
        builder.endField();
        return this;
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        checkSpace(BooleanType.LENGTH);
        builder.appendBoolean(value);
        return this;
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        checkSpace(ByteType.LENGTH);
        builder.appendByte(value);
        return this;
    }

    @Override
    public MessageBuilder appendChar(char value) {
        checkSpace(CharType.LENGTH);
        builder.appendChar(value);
        return this;
    }

    @Override
    public MessageBuilder appendInt(int value) {
        checkSpace(IntType.MAX_LENGTH);
        builder.appendInt(value);
        return this;
    }

    @Override
    public MessageBuilder appendLong(long value) {
        checkSpace(LongType.MAX_LENGTH);
        builder.appendLong(value);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        checkSpace(DoubleType.MAX_LENGTH);
        builder.appendDouble(value, precision);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        checkSpace(DoubleType.MAX_LENGTH);
        builder.appendDouble(value, precision, roundHalfUp);
        return this;
    }

    @Override
    public MessageBuilder appendTimestamp(long timestamp) {
        checkSpace(TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        builder.appendTimestamp(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        checkSpace(TimeType.MILLISECOND_TIME_LENGTH);
        builder.appendTime(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        checkSpace(DateType.LENGTH);
        builder.appendDate(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value) {
        checkSpace(value.length);
        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        checkSpace(length);
        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        checkSpace(value.capacity());
        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        checkSpace(length);
        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        checkSpace(value.length());
        builder.appendByteSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        checkSpace(length);
        builder.appendByteSequence(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        checkSpace(value.length());
        builder.appendCharSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        checkSpace(length);
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

    private void checkSpaceForField(int valueLength) {
        checkSpace(valueLength + IntType.MAX_UNSIGNED_INT_LENGTH + 2);
    }

    private void checkSpace(int space) {
        if (CHECK_BOUNDS) {
            int remaining = remaining();
            if (space > remaining)
                throw new InsufficientSpaceException(String.format("Insufficient free space %s in buffer, remaining %s", space, remaining));
        }
    }

}
