package org.efix.message.parser;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.MutableInt;
import org.efix.util.buffer.Buffer;
import org.efix.util.parse.*;

import static org.efix.message.FieldUtil.FIELD_SEPARATOR;
import static org.efix.message.FieldUtil.TAG_VALUE_SEPARATOR;


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
    public char parseChar() {
        return CharParser.parseChar(FIELD_SEPARATOR, buffer, offset, end);
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
    public long parseDecimal(int scale) {
        return DecimalParser.parseDecimal(scale, FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public long parseDecimal(int scale, boolean roundUp) {
        return DecimalParser.parseDecimal(scale, roundUp, FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public boolean parseBoolean() {
        return BooleanParser.parseBoolean(FIELD_SEPARATOR, buffer, offset, end);
    }

    @Override
    public CharSequence parseCharSequence() {
        return ByteSequenceParser.parseByteSequence(FIELD_SEPARATOR, buffer, offset, end, sequence);
    }

    @Override
    public ByteSequence parseByteSequence() {
        return ByteSequenceParser.parseByteSequence(FIELD_SEPARATOR, buffer, offset, end, sequence);
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
    public int start() {
        return start;
    }

    @Override
    public int end() {
        return end;
    }

    @Override
    public MessageParser offset(int offset) {
        this.offset.set(offset);
        return this;
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


