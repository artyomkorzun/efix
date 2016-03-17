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

    int offset();

    int length();

    int remaining();

    boolean hasRemaining();

}


