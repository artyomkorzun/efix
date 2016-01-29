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
        return builder.addBoolean(tag, value);
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        checkSpaceForField(ByteType.LENGTH);
        return builder.addByte(tag, value);
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        checkSpaceForField(CharType.LENGTH);
        return builder.addChar(tag, value);
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        checkSpaceForField(IntType.MAX_LENGTH);
        return builder.addInt(tag, value);
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        checkSpaceForField(LongType.MAX_LENGTH);
        return builder.addLong(tag, value);
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        checkSpaceForField(DoubleType.MAX_LENGTH);
        return builder.addDouble(tag, value, precision);
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        checkSpaceForField(DoubleType.MAX_LENGTH);
        return builder.addDouble(tag, value, precision, roundUp);
    }

    @Override
    public MessageBuilder addTimestamp(int tag, long timestamp) {
        checkSpaceForField(TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        return builder.addTimestamp(tag, timestamp);
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        checkSpaceForField(TimeType.MILLISECOND_TIME_LENGTH);
        return builder.addTime(tag, timestamp);
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        checkSpaceForField(DateType.LENGTH);
        return builder.addDate(tag, timestamp);
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        checkSpaceForField(value.length);
        return builder.addBytes(tag, value);
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int offset, int length) {
        checkSpaceForField(length);
        return builder.addBytes(tag, value, offset, length);
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        checkSpaceForField(value.capacity());
        return builder.addBytes(tag, value);
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int offset, int length) {
        checkSpaceForField(length);
        return builder.addBytes(tag, value, offset, length);
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        checkSpaceForField(value.length());
        return builder.addByteSequence(tag, value);
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length) {
        checkSpaceForField(length);
        return builder.addByteSequence(tag, value, offset, length);
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        checkSpaceForField(value.length());
        return builder.addCharSequence(tag, value);
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length) {
        checkSpaceForField(length);
        return builder.addCharSequence(tag, value, offset, length);
    }

    @Override
    public MessageBuilder startField(int tag) {
        checkSpace(IntType.MAX_UNSIGNED_INT_LENGTH + 1);
        return builder.startField(tag);
    }

    @Override
    public MessageBuilder endField() {
        checkSpace(1);
        return builder.endField();
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        checkSpace(BooleanType.LENGTH);
        return builder.appendBoolean(value);
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        checkSpace(ByteType.LENGTH);
        return builder.appendByte(value);
    }

    @Override
    public MessageBuilder appendChar(char value) {
        checkSpace(CharType.LENGTH);
        return builder.appendChar(value);
    }

    @Override
    public MessageBuilder appendInt(int value) {
        checkSpace(IntType.MAX_LENGTH);
        return builder.appendInt(value);
    }

    @Override
    public MessageBuilder appendLong(long value) {
        checkSpace(LongType.MAX_LENGTH);
        return builder.appendLong(value);
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        checkSpace(DoubleType.MAX_LENGTH);
        return builder.appendDouble(value, precision);
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        checkSpace(DoubleType.MAX_LENGTH);
        return builder.appendDouble(value, precision, roundHalfUp);
    }

    @Override
    public MessageBuilder appendTimestamp(long timestamp) {
        checkSpace(TimestampType.MILLISECOND_TIMESTAMP_LENGTH);
        return builder.appendTimestamp(timestamp);
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        checkSpace(TimeType.MILLISECOND_TIME_LENGTH);
        return builder.appendTime(timestamp);
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        checkSpace(DateType.LENGTH);
        return builder.appendDate(timestamp);
    }

    @Override
    public MessageBuilder appendBytes(byte[] value) {
        checkSpace(value.length);
        return builder.appendBytes(value);
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        checkSpace(length);
        return builder.appendBytes(value, offset, length);
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        checkSpace(value.capacity());
        return builder.appendBytes(value);
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        checkSpace(length);
        return builder.appendBytes(value, offset, length);
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        checkSpace(value.length());
        return builder.appendByteSequence(value);
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        checkSpace(length);
        return builder.appendByteSequence(value, offset, length);
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        checkSpace(value.length());
        return builder.appendCharSequence(value);
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        checkSpace(length);
        return builder.appendCharSequence(value, offset, length);
    }

    @Override
    public MessageBuilder reset() {
        return builder.reset();
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer) {
        return builder.wrap(buffer);
    }

    @Override
    public MessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        return builder.wrap(buffer, offset, length);
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
