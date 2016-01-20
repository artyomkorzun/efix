package org.f1x.message.parser;

import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.buffer.Buffer;

public interface MessageParser {

    int parseTag();


    byte parseByte();

    int parseInt();

    long parseLong();

    double parseDouble();

    boolean parseBoolean();

    CharSequence parseCharSequence();

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


