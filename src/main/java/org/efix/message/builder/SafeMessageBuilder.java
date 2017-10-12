package org.efix.message.builder;

import org.efix.message.FieldException;
import org.efix.util.ByteSequence;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.type.*;

/**
 * Checks bounds on add/append. Doesn't check bounds on wrap.
 * Checks string/sequence/bytes/byte/char values.
 */
public class SafeMessageBuilder implements MessageBuilder {

    public static final String DISABLE_BOUNDS_CHECK_PROP_KEY = "efix.safe.message.builder.disable.bounds.check";
    public static final String DISABLE_VALUES_CHECK_PROP_KEY = "efix.safe.message.builder.disable.values.check";

    protected static final boolean CHECK_BOUNDS = !Boolean.getBoolean(DISABLE_BOUNDS_CHECK_PROP_KEY);
    protected static final boolean CHECK_VALUES = !Boolean.getBoolean(DISABLE_VALUES_CHECK_PROP_KEY);

    protected final MessageBuilder builder;

    protected SafeMessageBuilder(MessageBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MessageBuilder addBoolean(int tag, boolean value) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + BooleanType.LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addBoolean(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByte(int tag, byte value) {
        if (CHECK_VALUES) {
            if (isNonAlphaNumeric(value)) {
                throw new FieldException(tag, "Not valid byte: " + (char) value);
            }
        }

        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + ByteType.LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addByte(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addChar(int tag, char value) {
        if (CHECK_VALUES) {
            if (isNonAlphaNumeric(value)) {
                throw new FieldException(tag, "Not valid char: " + value);
            }
        }

        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + CharType.LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addChar(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addInt(int tag, int value) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + IntType.MAX_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addInt(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addLong(int tag, long value) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + LongType.MAX_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addLong(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + DoubleType.MAX_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addDouble(tag, value, precision);
        return this;
    }

    @Override
    public MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + DoubleType.MAX_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addDouble(tag, value, precision, roundUp);
        return this;
    }

    @Override
    public MessageBuilder addDecimal(int tag, long value, int scale) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + DecimalType.MAX_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addDecimal(tag, value, scale);
        return this;
    }

    @Override
    public MessageBuilder addTimestampMs(int tag, long timestampMs) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + TimestampType.MILLISECOND_TIMESTAMP_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addTimestampMs(tag, timestampMs);
        return this;
    }

    @Override
    public MessageBuilder addTimestampNs(int tag, long timestampNs) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + TimestampType.NANOSECOND_TIMESTAMP_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addTimestampNs(tag, timestampNs);
        return this;
    }

    @Override
    public MessageBuilder addTime(int tag, long timestamp) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + TimeType.MILLISECOND_TIME_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addTime(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addDate(int tag, long timestamp) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH + DateType.LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addDate(tag, timestamp);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value) {
        if (CHECK_VALUES) {
            if (value.length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (byte b : value) {
                if (isNonAlphaNumeric(b)) {
                    throw new FieldException(tag, "Not valid bytes: " + new String(value));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + value.length + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, byte[] value, int offset, int length) {
        if (CHECK_VALUES) {
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = offset, end = offset + length; i < end; i++) {
                if (isNonAlphaNumeric(value[i])) {
                    throw new FieldException(tag, "Not valid bytes: " + new String(value, offset, length));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + length + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value) {
        if (CHECK_VALUES) {
            final int capacity = value.capacity();
            if (capacity <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = 0; i < capacity; i++) {
                if (isNonAlphaNumeric(value.getByte(i))) {
                    throw new FieldException(tag, "Not valid bytes: " + BufferUtil.toString(value));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + value.capacity() + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addBytes(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addBytes(int tag, Buffer value, int offset, int length) {
        if (CHECK_VALUES) {
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = offset, end = offset + length; i < end; i++) {
                if (isNonAlphaNumeric(value.getByte(i))) {
                    throw new FieldException(tag, "Not valid bytes: " + BufferUtil.toString(value, offset, length));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + length + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addBytes(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value) {
        if (CHECK_VALUES) {
            final int length = value.length();
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = 0; i < length; i++) {
                if (isNonAlphaNumeric(value.byteAt(i))) {
                    throw new FieldException(tag, "Not valid sequence: " + value);
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + value.length() + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addByteSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length) {
        if (CHECK_VALUES) {
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = offset, end = offset + length; i < end; i++) {
                if (isNonAlphaNumeric(value.byteAt(i))) {
                    throw new FieldException(tag, "Not valid sequence: " + value.subSequence(offset, end));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + length + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addByteSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value) {
        if (CHECK_VALUES) {
            final int length = value.length();
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = 0; i < length; i++) {
                if (isNonAlphaNumeric(value.charAt(i))) {
                    throw new FieldException(tag, "Not valid sequence: " + value);
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + value.length() + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addCharSequence(tag, value);
        return this;
    }

    @Override
    public MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length) {
        if (CHECK_VALUES) {
            if (length <= 0) {
                throw new FieldException(tag, "Empty value");
            }

            for (int i = offset, end = offset + length; i < end; i++) {
                if (isNonAlphaNumeric(value.charAt(i))) {
                    throw new FieldException(tag, "Not valid sequence: " + value.subSequence(offset, end));
                }
            }
        }

        if (CHECK_BOUNDS) {
            final long required = (long) IntType.MAX_UINT_LENGTH + ByteType.LENGTH + length + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.addCharSequence(tag, value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder startField(int tag) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_UINT_LENGTH + ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.startField(tag);
        return this;
    }

    @Override
    public MessageBuilder endField() {
        if (CHECK_BOUNDS) {
            final int required = ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.endField();
        return this;
    }

    @Override
    public MessageBuilder appendBoolean(boolean value) {
        if (CHECK_BOUNDS) {
            final int required = BooleanType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendBoolean(value);
        return this;
    }

    @Override
    public MessageBuilder appendByte(byte value) {
        if (CHECK_BOUNDS) {
            final int required = ByteType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendByte(value);
        return this;
    }

    @Override
    public MessageBuilder appendChar(char value) {
        if (CHECK_BOUNDS) {
            final int required = CharType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendChar(value);
        return this;
    }

    @Override
    public MessageBuilder appendInt(int value) {
        if (CHECK_BOUNDS) {
            final int required = IntType.MAX_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendInt(value);
        return this;
    }

    @Override
    public MessageBuilder appendLong(long value) {
        if (CHECK_BOUNDS) {
            final int required = LongType.MAX_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendLong(value);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision) {
        if (CHECK_BOUNDS) {
            final int required = DoubleType.MAX_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendDouble(value, precision);
        return this;
    }

    @Override
    public MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp) {
        if (CHECK_BOUNDS) {
            final int required = DoubleType.MAX_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendDouble(value, precision, roundHalfUp);
        return this;
    }

    @Override
    public MessageBuilder appendDecimal(long value, int scale) {
        if (CHECK_BOUNDS) {
            final int required = DecimalType.MAX_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendDecimal(value, scale);
        return this;
    }

    @Override
    public MessageBuilder appendTimestampMs(long timestampMs) {
        if (CHECK_BOUNDS) {
            final int required = TimestampType.MILLISECOND_TIMESTAMP_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendTimestampMs(timestampMs);
        return this;
    }

    @Override
    public MessageBuilder appendTimestampNs(final long timestampNs) {
        if (CHECK_BOUNDS) {
            final int required = TimestampType.NANOSECOND_TIMESTAMP_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendTimestampNs(timestampNs);
        return this;
    }

    @Override
    public MessageBuilder appendTime(long timestamp) {
        if (CHECK_BOUNDS) {
            final int required = TimeType.MILLISECOND_TIME_LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }
        builder.appendTime(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendDate(long timestamp) {
        if (CHECK_BOUNDS) {
            final long required = DateType.LENGTH;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }
        builder.appendDate(timestamp);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value) {
        if (CHECK_BOUNDS) {
            final int required = value.length;
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(byte[] value, int offset, int length) {
        if (CHECK_BOUNDS) {
            final int remaining = remaining();
            if (length > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", length, remaining));
            }
        }

        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value) {
        if (CHECK_BOUNDS) {
            final int capacity = value.capacity();
            final int remaining = remaining();

            if (capacity > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", capacity, remaining));
            }
        }

        builder.appendBytes(value);
        return this;
    }

    @Override
    public MessageBuilder appendBytes(Buffer value, int offset, int length) {
        if (CHECK_BOUNDS) {
            final int remaining = remaining();
            if (length > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", length, remaining));
            }
        }

        builder.appendBytes(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value) {
        if (CHECK_BOUNDS) {
            final int length = value.length();
            final int remaining = remaining();

            if (length > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", length, remaining));
            }
        }

        builder.appendByteSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        if (CHECK_BOUNDS) {
            final int remaining = remaining();
            if (length > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", length, remaining));
            }
        }

        builder.appendByteSequence(value, offset, length);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value) {
        if (CHECK_BOUNDS) {
            final int required = value.length();
            final int remaining = remaining();

            if (required > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", required, remaining));
            }
        }

        builder.appendCharSequence(value);
        return this;
    }

    @Override
    public MessageBuilder appendCharSequence(CharSequence value, int offset, int length) {
        if (CHECK_BOUNDS) {
            final int remaining = remaining();
            if (length > remaining) {
                throw new InsufficientSpaceException(String.format("Insufficient space in buffer, required %s, remaining %s", length, remaining));
            }
        }

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

    public static SafeMessageBuilder wrap(MessageBuilder builder) {
        return new SafeMessageBuilder(builder);
    }

    private static boolean isNonAlphaNumeric(byte value) {
        return (value < ' ') | (value > '~');
    }

    private static boolean isNonAlphaNumeric(char value) {
        return (value < ' ') | (value > '~');
    }

}
