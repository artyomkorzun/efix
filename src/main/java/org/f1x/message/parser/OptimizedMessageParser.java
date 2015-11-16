package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.parse.NumbersParser;
import org.f1x.util.parse.TimeOfDayParser;
import org.f1x.util.parse.TimestampParser;

public class OptimizedMessageParser implements MessageParser {

    private static final byte TAG_VALUE_SEPARATOR = '=';
    private static final byte FIELD_SEPARATOR = 1;

    private final TimestampParser utcTimestampParser = TimestampParser.createUTCTimestampParser();
    private final TimestampParser localTimestampParser = TimestampParser.createLocalTimestampParser();
    private final ByteSequence byteSequence = new ByteSequence();

    private Buffer buffer;
    private int offset;
    private int start;
    private int end;
    private int tag;
    private int fieldOffset;
    private int valueOffset;
    private int valueLength;

    public OptimizedMessageParser wrap(Buffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.start = offset;
        this.end = offset + length;
        this.offset = offset;
        this.tag = 0;
        this.fieldOffset = offset;
        this.valueOffset = 0;
        this.valueLength = 0;

        return this;
    }

    @Override
    public boolean next() {
        int offset = this.offset;
        int end = this.end;
        if (offset == end)
            return false;

        Buffer buffer = this.buffer;
        int fieldOffset = offset;
        int tag = 0;
        while (offset < end) {
            byte b = buffer.getByte(offset++);
            if (b == TAG_VALUE_SEPARATOR)
                break;

            tag = (tag << 3) + (tag << 1) + (b - '0');
        }

        int tagLength = offset - fieldOffset - 1;
        if (tagLength == 0)
            throw new FixParserException("Tag is empty");

        /*int valueOffset = offset;
        offset = findByte(FIELD_SEPARATOR, buffer, valueOffset, end);

        int valueLength = offset - valueOffset;
        if (valueLength == 0)
            throw new FixParserException("Value is empty");*/

        this.tag = tag;
        this.offset = offset;
        this.fieldOffset = fieldOffset;

        return true;

    }

    @Override
    public int tag() {
        return tag;
    }

    @Override
    public byte byteValue() {
        if (valueLength > 1)
            throw new FixParserException("Value is not a single byte");

        return buffer.getByte(valueOffset);
    }

    @Override
    public int intValue() {
        int offset = this.offset;
        int end = this.end;
        int value = 0;
        while (offset < end) {
            byte b = buffer.getByte(offset++);
            if (b == FIELD_SEPARATOR)
                break;

            value = (value << 3) + (value << 1) + b - '0';
        }

        this.offset = offset;

        return value;
    }

    @Override
    public long longValue() {
        return NumbersParser.parseLong(buffer, valueOffset, valueLength);
    }

    @Override
    public double doubleValue() {
        return NumbersParser.parseDouble(buffer, valueOffset, valueLength);
    }

    @Override
    public CharSequence charSequence() {
        return byteSequence.wrap(buffer, valueOffset, valueLength);
    }

    @Override
    public void byteSequence(ByteSequence sequence) {
        sequence.wrap(buffer, valueOffset, valueLength);
    }

    @Override
    public String string() {
        char[] chars = new char[valueLength];
        for (int i = 0; i < valueLength; i++)
            chars[i] = (char) buffer.getByte(valueOffset + i);

        return new String(chars);
    }

    @Override
    public boolean booleanValue() {
        if (valueLength > 1)
            throw new FixParserException("Field is not a character");

        if (buffer.getByte(valueOffset) == 'Y') return true;

        if (buffer.getByte(valueOffset) == 'N') return false;

        throw new FixParserException("Field cannot be parsed as FIX boolean");
    }

    @Override
    public long utcTimestamp() {
        return utcTimestampParser.getUTCTimestampValue(buffer, valueOffset, valueLength);
    }

    @Override
    public long utcDate() {
        return utcTimestampParser.getUTCDateOnly(buffer, valueOffset, valueLength);
    }

    @Override
    public long localDate() {
        return localTimestampParser.getUTCDateOnly(buffer, valueOffset, valueLength);
    }

    @Override
    public int utcTime() {
        return TimeOfDayParser.parseTimeOfDay(buffer, valueOffset, valueLength);
    }

    @Override
    public OptimizedMessageParser reset() {
        tag = valueOffset = valueLength = 0;
        offset = start;

        return this;
    }

    @Override
    public int fieldOffset() {
        return fieldOffset;
    }

    @Override
    public int fieldLength() {
        return valueLength;
    }

    protected static int findByte(byte b, Buffer buffer, int offset, int end) {
        for (; offset < end; offset++) {
            if (b == buffer.getByte(offset))
                return offset;
        }

        throw new FixParserException(String.format("%s not found", (char) b));
    }

}


