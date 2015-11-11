/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.f1x.message.parser;

import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.parse.NumbersParser;
import org.f1x.util.parse.TimeOfDayParser;
import org.f1x.util.parse.TimestampParser;

public class DefaultMessageParser implements MessageParser {

    //private static final GFLog LOGGER = GFLogFactory.getLog(DefaultMessageParser.class);

    private static final char SOH = 1; // field separator

    private final TimestampParser utcTimestampParser = TimestampParser.createUTCTimestampParser();
    private final TimestampParser localTimestampParser = TimestampParser.createLocalTimestampParser();
    private final ByteSequence charSequenceBuffer = new ByteSequence();

    private Buffer buffer;
    private int start;
    private int offset; // next byte to read
    private int limit;
    private int tagNum;
    private int valueOffset, valueLength;


    public final DefaultMessageParser wrap(Buffer buffer) {
        this.buffer = buffer;
        this.start = 0;
        this.limit = buffer.capacity();
        reset();

        return this;
    }

    public DefaultMessageParser wrap(Buffer buffer, int offset, int length) {
        buffer.checkBounds(offset, length);
        this.buffer = buffer;
        this.start = offset;
        this.limit = offset + length;
        reset();

        return this;
    }

    @Override
    public final boolean next() {
        try {
            final boolean result = _next();
            if (result) {
                if (valueLength == 0)
                    throw new FixParserException("Tag " + tagNum + " has empty value at position " + offset);

            }
            return result;

        } catch (FixParserException e) {
            throw new FixParserException("Parser error (at " + offset + "): " + e.getMessage());
        }
    }

    private boolean _next() {
        boolean isParsingTagNum = true;
        tagNum = 0;
        while (offset < limit) {
            byte ch = buffer.getByte(offset++);
            if (isParsingTagNum) {
                if (ch >= '0' && ch <= '9') {
                    tagNum = 10 * tagNum + (ch - '0');
                } else if (ch == '=') {
                    if (tagNum == 0)
                        throw new FixParserException("Unexpected '=' character instead of a tag number digit");
                    isParsingTagNum = false;
                    valueOffset = offset;
                    valueLength = 0;
                } else {
                    throw new FixParserException("Unexpected character (0x" + Integer.toHexString(ch) + " where a tag number digit or '=' is expected");
                }

            } else {
                if (ch == SOH)
                    return true;

                valueLength++;
            }
        }
        return false;
    }


    @Override
    public int getTagNum() {
        return tagNum;
    }

    @Override
    public byte getByteValue() {
        if (valueLength > 1)
            throw new FixParserException("Value is not a single byte");

        return buffer.getByte(valueOffset);
    }

    @Override
    public int getIntValue() {
        return NumbersParser.parseInt(buffer, valueOffset, valueLength);
    }

    @Override
    public long getLongValue() {
        return NumbersParser.parseLong(buffer, valueOffset, valueLength);
    }

    @Override
    public double getDoubleValue() {
        return NumbersParser.parseDouble(buffer, valueOffset, valueLength);
    }

    @Override
    public CharSequence getCharSequenceValue() {
        charSequenceBuffer.wrap(buffer, valueOffset, valueLength);
        return charSequenceBuffer;
    }

    @Override
    public void getByteSequence(ByteSequence seq) {
        seq.wrap(buffer, valueOffset, valueLength);
    }

    @Override
    public String getStringValue() {
        char[] chars = new char[valueLength];
        for (int i = 0; i < valueLength; i++)
            chars[i] = (char) buffer.getByte(valueOffset + i);

        return new String(chars);
    }

    @Override
    public void getStringBuilder(StringBuilder appendable) {
        charSequenceBuffer.wrap(buffer, valueOffset, valueLength);
        appendable.append(charSequenceBuffer);
    }

    @Override
    public boolean getBooleanValue() {
        if (valueLength > 1)
            throw new FixParserException("Field is not a character");

        if (buffer.getByte(valueOffset) == 'Y') return true;

        if (buffer.getByte(valueOffset) == 'N') return false;

        throw new FixParserException("Field cannot be parsed as FIX boolean");
    }

    @Override
    public long getUTCTimestampValue() {
        return utcTimestampParser.getUTCTimestampValue(buffer, valueOffset, valueLength);
    }

    @Override
    public long getUTCDateOnly() {
        return utcTimestampParser.getUTCDateOnly(buffer, valueOffset, valueLength);
    }

    @Override
    public long getLocalMktDate() {
        return localTimestampParser.getUTCDateOnly(buffer, valueOffset, valueLength);
    }


    @Override
    public int getLocalMktDate2() {
        return localTimestampParser.getUTCDateOnly2(buffer, valueOffset, valueLength);
    }

    @Override
    public int getUTCTimeOnly() {
        return TimeOfDayParser.parseTimeOfDay(buffer, valueOffset, valueLength);
    }

    @Override
    public boolean isValueEquals(byte[] constant) {
        if (valueLength != constant.length)
            return false;

        for (int i = 0; i < valueLength; i++)
            if (buffer.getByte(valueOffset + i) != constant[i])
                return false;

        return true;
    }

    @Override
    public final DefaultMessageParser reset() {
        tagNum = valueOffset = valueLength = 0;
        offset = start;

        return this;
    }

    @Override
    public int fieldOffset() {
        return offset;
    }

    @Override
    public int fieldLength() {
        return valueOffset;
    }

    /* @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append ("Buffer pos ");
        sb.append (offset);
        sb.append ('/');
        sb.append (limit);
        sb.append (" Current tag ");
        sb.append (tagNum);
        if (valueLength > 0) {
            sb.append ('=');
            sb.append (new String(buffer, valueOffset, valueLength));
        }
        return sb.toString();
    }*/
}


