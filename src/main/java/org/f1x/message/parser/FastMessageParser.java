package org.f1x.message.parser;

import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.parse.*;

import static org.f1x.message.FieldUtil.FIELD_SEPARATOR;
import static org.f1x.message.FieldUtil.TAG_VALUE_SEPARATOR;

public class FastMessageParser implements MessageParser {

    protected final ByteSequenceWrapper sequence = new ByteSequenceWrapper();
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
    public void parseByteSequence(ByteSequenceWrapper sequence) {
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
        this.offset.set(offset);
        this.start = offset;
        this.end = offset + length;
        return this;
    }

    @Override
    public MessageParser reset() {
        offset.set(start);
        return this;
    }

    @Override
    public Buffer buffer() {
        return buffer;
    }

    @Override
    public int offset() {
        return offset.get();
    }

    @Override
    public int length() {
        return offset.get() - start;
    }

    @Override
    public int remaining() {
        return end - offset.get();
    }

    @Override
    public boolean hasRemaining() {
        return offset.get() < end;
    }

}


