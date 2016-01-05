package org.f1x.message.builder.newone;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public interface MessageBuilder {

    default void addByte(int tag, byte value) {
        startField(tag).appendByte(value).endField();
    }

    default void addBoolean(int tag, boolean value) {
        startField(tag).appendBoolean(value).endField();
    }


    default void addInt(int tag, int value) {
        startField(tag).appendInt(value).endField();
    }

    default void addLong(int tag, long value) {
        startField(tag).appendLong(value).endField();
    }


    default void addDouble(int tag, double value, int precision) {
        startField(tag).appendDouble(value, precision).endField();
    }

    default void addDouble(int tag, double value, int precision, boolean roundUp) {
        startField(tag).appendDouble(value, precision, roundUp).endField();
    }


    default void addTimestamp(int tag, long timestamp) {
        startField(tag).appendTimestamp(timestamp).endField();
    }

    default void addTime(int tag, long timestamp) {
        startField(tag).appendTime(timestamp).endField();
    }

    default void addDate(int tag, long timestamp) {
        startField(tag).appendDate(timestamp).endField();
    }


    default void addBytes(int tag, byte[] value) {
        addBytes(tag, value, 0, value.length);
    }

    default void addBytes(int tag, byte[] value, int offset, int length) {
        startField(tag).appendBytes(value, offset, length).endField();
    }


    default void addBytes(int tag, Buffer value) {
        addBytes(tag, value, 0, value.capacity());
    }

    default void addBytes(int tag, Buffer value, int offset, int length) {
        startField(tag).appendBytes(value, offset, length).endField();
    }

    default void addByteSequence(int tag, ByteSequence value) {
        addByteSequence(tag, value, 0, value.length());
    }

    default void addByteSequence(int tag, ByteSequence value, int offset, int length) {
        startField(tag).appendByteSequence(value, offset, length).endField();
    }


    default void addCharSequenceField(int tag, CharSequence value) {
        addCharSequenceField(tag, value, 0, value.length());
    }

    default void addCharSequenceField(int tag, CharSequence value, int offset, int length) {
        startField(tag).appendCharSequence(value, offset, length).endField();
    }


    MessageBuilder startField(int tag);

    void endField();


    MessageBuilder appendByte(byte value);

    MessageBuilder appendBoolean(boolean value);


    MessageBuilder appendInt(int value);

    MessageBuilder appendLong(long value);


    MessageBuilder appendDouble(double value, int precision);

    MessageBuilder appendDouble(double value, int precision, boolean roundUp);


    MessageBuilder appendTimestamp(long timestamp);

    MessageBuilder appendTime(long timestamp);

    MessageBuilder appendDate(long timestamp);


    default MessageBuilder appendBytes(byte[] value) {
        appendBytes(value, 0, value.length);
        return this;
    }

    MessageBuilder appendBytes(byte[] value, int offset, int length);


    default MessageBuilder appendBytes(Buffer value) {
        appendBytes(value, 0, value.capacity());
        return this;
    }

    MessageBuilder appendBytes(Buffer value, int offset, int length);


    default MessageBuilder appendByteSequence(ByteSequence value) {
        appendByteSequence(value, 0, value.length());
        return this;
    }

    default MessageBuilder appendByteSequence(ByteSequence value, int offset, int length) {
        appendBytes(value.wrapper(), offset, length);
        return this;
    }


    default MessageBuilder appendCharSequence(CharSequence value) {
        appendCharSequence(value, 0, value.length());
        return this;
    }

    MessageBuilder appendCharSequence(CharSequence value, int offset, int length);


    int length();

    MessageBuilder reset();


    default MessageBuilder wrap(MutableBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
        return this;
    }

    MessageBuilder wrap(MutableBuffer buffer, int offset, int length);


}
