package org.efix.message.builder;

import org.efix.util.ByteSequence;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;


public interface MessageBuilder {

    MessageBuilder addBoolean(int tag, boolean value);

    MessageBuilder addByte(int tag, byte value);

    MessageBuilder addChar(int tag, char value);


    MessageBuilder addInt(int tag, int value);

    MessageBuilder addLong(int tag, long value);


    MessageBuilder addDouble(int tag, double value, int precision);

    MessageBuilder addDouble(int tag, double value, int precision, boolean roundUp);


    MessageBuilder addDecimal(int tag, long value, int scale);


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


    MessageBuilder appendDecimal(long value, int scale);


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


    MutableBuffer buffer();

    /**
     * @return offset from which message starts
     */
    int start();

    /**
     * @return offset where buffer ends + 1
     */
    int end();

    /**
     * @param offset from which continue appending message
     */
    MessageBuilder offset(int offset);

    /**
     * @return offset from which continue appending message
     */
    int offset();

    /**
     * @return length of appended chunk of message
     */
    int length();

    /**
     * @return length of free space in buffer
     */
    int remaining();

}
