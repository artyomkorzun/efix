package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;

/**
 * Streaming parser. Example:
 * while(parser.hasRemaining()){
 * int tag = parser.parseTag();
 * switch(tag) {
 * case 55:
 * CharSequence symbol = parser.parseCharSequence();
 * break;
 * default:
 * parser.parseValue();
 * }
 * }
 */
public interface StreamMessageParser {

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

    void parseValue();

    int offset();

    int length();

    int remaining();

    boolean hasRemaining();

    default StreamMessageParser wrap(Buffer buffer){
        return wrap(buffer, 0, buffer.capacity());
    }

    StreamMessageParser wrap(Buffer buffer, int offset, int length);

    StreamMessageParser reset();


}
