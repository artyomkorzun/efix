package org.efix.message.parser;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;


public interface MessageParser {

    int parseTag();


    byte parseByte();

    char parseChar();

    int parseInt();

    long parseLong();

    double parseDouble();

    long parseDecimal(int scale);

    long parseDecimal(int scale, boolean roundUp);

    boolean parseBoolean();

    CharSequence parseCharSequence();

    ByteSequence parseByteSequence();

    void parseByteSequence(ByteSequenceWrapper sequence);

    int parseTime();

    long parseDate();

    long parseTimestamp();

    void parseValue();


    MessageParser wrap(Buffer buffer);

    MessageParser wrap(Buffer buffer, int offset, int length);

    MessageParser reset();


    Buffer buffer();

    /**
     * @return offset from which the current message starts
     */
    int start();

    /**
     * @return offset from which the next message starts
     */
    int end();

    /**
     * @param offset from which continue parsing message
     */
    MessageParser offset(int offset);

    /**
     * @return offset from which to continue parsing message
     */
    int offset();

    /**
     * @return length of parsed chunk of message
     */
    int length();

    /**
     * @return length of not parsed chunk of message
     */
    int remaining();

    boolean hasRemaining();

}


