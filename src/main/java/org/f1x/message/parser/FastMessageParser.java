package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.parse.*;

import static org.f1x.message.FieldUtil.FIELD_SEPARATOR;
import static org.f1x.message.FieldUtil.TAG_VALUE_SEPARATOR;

public class FastMessageParser implements MessageParser {

    protected final ByteSequence sequence = new ByteSequence();
    protected final MutableInt offset = new MutableInt();

    protected Buffer buffer;
    protected int start;
    protected int end;

    @Override
    public int parseTag() {
        return IntParser.parseUInt(TAG_VALUE_SEPARATOR, buffer, offset, end);
    }

    @Override
    public byte parseByte() {
        return ByteParser.parseByte(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public int parseInt() {
        return IntParser.parseInt(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public long parseLong() {
        return LongParser.parseLong(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public double parseDouble() {
        return DoubleParser.parseDouble(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public boolean parseBoolean() {
        return BooleanParser.parseBoolean(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public CharSequence parseCharSequence() {
        parseByteSequence(sequence);
        return sequence;
    }

    @Override
    public void parseByteSequence(ByteSequence sequence) {
        ByteSequenceParser.parseByteSequence(FIELD_SEPARATOR, buffer, offset, end, sequence);
    }

    @Override
    public int parseTime() {
        return TimeParser.parseTime(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public long parseDate() {
        return DateParser.parseDate(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public long parseTimestamp() {
        return TimestampParser.parseTimestamp(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public void parseValue() {
        ByteSequenceParser.parseByteSequence(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public MessageParser wrap(Buffer buffer) {
        return wrap(buffer, 0, buffer.capacity());
    }

    @Override
    public MessageParser wrap(Buffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset.value(offset);
        this.start = offset;
        this.end = offset + length;
        return this;
    }

    @Override
    public MessageParser reset() {
        offset.value(start);
        return this;
    }

    @Override
    public int offset() {
        return offset.value();
    }

    @Override
    public int length() {
        return offset.value() - start;
    }

    @Override
    public int remaining() {
        return end - offset.value();
    }

    @Override
    public boolean hasRemaining() {
        return offset.value() < end;
    }

}


