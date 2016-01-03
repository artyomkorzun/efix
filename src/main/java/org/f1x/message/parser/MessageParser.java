package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

public interface MessageParser {

    int parseTag();

    byte parseByte();

    int parseInt();

    long parseLong();

    double parseDouble();

    boolean parseBoolean();

    CharSequence parseCharSequence();

    void parseByteSequence(ByteSequence sequence);

    int parseTime();

    long parseDate();

    long parseTimestamp();

    void parseValue();

    int offset();

    int length();

    int remaining();

    boolean hasRemaining();

    default MessageParser wrap(Buffer buffer){
        return wrap(buffer, 0, buffer.capacity());
    }

    MessageParser wrap(Buffer buffer, int offset, int length);

    MessageParser reset();

}


