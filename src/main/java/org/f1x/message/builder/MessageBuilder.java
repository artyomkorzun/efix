package org.f1x.message.builder;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public interface MessageBuilder {

    MessageBuilder addBoolean(int tag, boolean value);

    MessageBuilder addByte(int tag, byte value);

    MessageBuilder addChar(int tag, char value);


    MessageBuilder addInt(int tag, int value);

    MessageBuilder addLong(int tag, long value);


    MessageBuilder addDouble(int tag, double value, int precision);

    MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp);


    MessageBuilder addTimestamp(int tag, long timestamp);

    MessageBuilder addTime(int tag, long timestamp);

    MessageBuilder addDate(int tag, long timestamp);


    MessageBuilder addBytes(int tag, byte[] value);

    MessageBuilder addBytes(int tag, byte[] value, int offset, int length);


    MessageBuilder addBytes(int tag, Buffer value);

    MessageBuilder addBytes(int tag, Buffer value, int offset, int length);


    MessageBuilder addByteSequence(int tag, ByteSequence value);

    MessageBuilder addByteSequence(int tag, ByteSequence value, int offset, int length);


    MessageBuilder addCharSequence(int tag, CharSequence value);

    MessageBuilder addCharSequence(int tag, CharSequence value, int offset, int length);


    MessageBuilder startField(int tag);

    MessageBuilder endField();


    MessageBuilder appendBoolean(boolean value);

    MessageBuilder appendByte(byte value);

    MessageBuilder appendChar(char value);


    MessageBuilder appendInt(int value);

    MessageBuilder appendLong(long value);


    MessageBuilder appendDouble(double value, int precision);

    MessageBuilder appendDouble(double value, int precision, boolean roundHalfUp);


    MessageBuilder appendTimestamp(long timestamp);

    MessageBuilder appendTime(long timestamp);

    MessageBuilder appendDate(long timestamp);


    MessageBuilder appendBytes(byte[] value);

    MessageBuilder appendBytes(byte[] value, int offset, int length);


    MessageBuilder appendBytes(Buffer value);

    MessageBuilder appendBytes(Buffer value, int offset, int length);


    MessageBuilder appendByteSequence(ByteSequence value);

    MessageBuilder appendByteSequence(ByteSequence value, int offset, int length);


    MessageBuilder appendCharSequence(CharSequence value);

    MessageBuilder appendCharSequence(CharSequence value, int offset, int length);


    MessageBuilder reset();

    MessageBuilder wrap(MutableBuffer buffer);

    MessageBuilder wrap(MutableBuffer buffer, int offset, int length);


    int offset();

    int length();

    int remaining();

}
