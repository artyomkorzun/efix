package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

public interface NewMessageParser {

    int parseTag();


    byte parseByte();

    int parseInt();

    long parseLong();

    double parseDouble();

    boolean parseBoolean();

    CharSequence parseCharSequence();

    void parseByteSequence(ByteSequence sequence);

    String parseString();

    long parseUTCTimestamp();

    int parseUTCTime();

    long parseUTCDate();

    long parseLocalDate();


    int offset();

    int length();

    int remaining();

    boolean hasRemaining();


    default NewMessageParser wrap(Buffer buffer){
        return wrap(buffer, 0, buffer.capacity());
    }

    NewMessageParser wrap(Buffer buffer, int offset, int length);

    NewMessageParser reset();


}
