package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

public interface MessageParser {

    boolean next();

    int tag();

    byte byteValue();

    int intValue();

    long longValue();

    double doubleValue();

    boolean booleanValue();

    CharSequence charSequence();

    void byteSequence(ByteSequence sequence);

    String string();

    long utcTimestamp();

    int utcTime();

    long utcDate();

    long localDate();

    int fieldOffset();

    int fieldLength();

    default MessageParser wrap(Buffer buffer){
        return wrap(buffer, 0, buffer.capacity());
    }

    MessageParser wrap(Buffer buffer, int offset, int length);

    MessageParser reset();

}


