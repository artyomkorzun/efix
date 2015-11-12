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

package org.f1x.message.builder;

import org.f1x.message.AppendableValue;
import org.f1x.message.Fields;
import org.f1x.message.types.ByteEnum;
import org.f1x.message.types.IntEnum;
import org.f1x.message.types.StringEnum;
import org.f1x.util.ByteSequence;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.format.*;

public final class FastMessageBuilder implements MessageBuilder {

    private static final byte BYTE_Y = 'Y';
    private static final byte BYTE_N = 'N';
    private static final byte SOH = 1; // field separator
    public static final String NULL = "null";

    private final TimestampFormatter gmtTimestampFormat = TimestampFormatter.createUTCTimestampFormatter();
    private final TimestampFormatter localTimestampFormat = TimestampFormatter.createLocalTimestampFormatter();
    private final DoubleFormatter doubleFormatter;

    private CharSequence msgType;
    private MutableBuffer buffer;
    private int offset;
    private int start;

    public FastMessageBuilder(int maxLength, int doubleFormatterPrecision) {
        this(new byte[maxLength], doubleFormatterPrecision);
    }

    public FastMessageBuilder(byte[] buff, int doubleFormatterPrecision) {
        buffer = new UnsafeBuffer(buff);
        doubleFormatter = new DoubleFormatter(doubleFormatterPrecision);
    }

    public FastMessageBuilder(int doubleFormatterPrecision) {
        doubleFormatter = new DoubleFormatter(doubleFormatterPrecision);
    }

    @Override
    public MessageBuilder clear() {
        offset = 0;
        return this;
    }

    @Override
    public void add(int tagNo, final CharSequence value) {
        checkValue(tagNo, value);

        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = CharSequenceFormatter.format(value, buffer, offset);
        addFieldSeparator();
    }


    @Override
    public void add(int tagNo, CharSequence value, int start, int end) {
        checkValue(tagNo, value, start, end);

        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = CharSequenceFormatter.format(value, start, end, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, long value) {
        offset = LongFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = LongFormatter.format(value, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, int value) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = IntFormatter.format(value, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, double value) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = doubleFormatter.format(value, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, double value, int precision) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = doubleFormatter.format(value, precision, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, double value, int precision, boolean roundUp) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = doubleFormatter.format(value, precision, roundUp, DoubleFormatter.MAX_WIDTH, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, byte value) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        buffer.putByte(offset++, value);
        addFieldSeparator();
    }

    @Override
    public void add(int tagNo, boolean value) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        buffer.putByte(offset++, value ? BYTE_Y : BYTE_N);
        addFieldSeparator();
    }

    @Override
    public void addUTCTimestamp(int tagNo, long timestamp) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = gmtTimestampFormat.formatDateTime(timestamp, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void addUTCDateOnly(int tagNo, long timestamp) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = gmtTimestampFormat.formatDateOnly(timestamp, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void addLocalMktDate(int tagNo, long timestamp) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = localTimestampFormat.formatDateOnly(timestamp, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void addLocalMktDate2(int tagNo, int yyyymmdd) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = IntFormatter.format4digits(yyyymmdd / 10000, buffer, offset); // year
        int mmdd = yyyymmdd % 10000;
        offset = IntFormatter.format2digits(mmdd / 100, buffer, offset); // month
        offset = IntFormatter.format2digits(mmdd % 100, buffer, offset); // day
        addFieldSeparator();
    }

    @Override
    public void addUTCTimeOnly(int tagNo, long timestamp) {
        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        offset = TimeOfDayFormatter.format(timestamp, buffer, offset);
        addFieldSeparator();
    }

    @Override
    public void addRaw(int tagNo, byte[] sourceBuffer, int sourceOffset, int sourceLength) {
        checkValue(tagNo, sourceBuffer, sourceOffset, sourceLength);

        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        buffer.putBytes(offset, sourceBuffer, sourceOffset, sourceLength);
        offset += sourceLength;
        addFieldSeparator();
    }

    @Override
    public void addRaw(int tagNo, ByteSequence sequence) {
        checkValue(sequence);

        offset = IntFormatter.format(tagNo, buffer, offset);
        addTagValueSeparator();
        append(sequence.wrapper());
        addFieldSeparator();
    }

    @Override
    public void add(int tag, ByteEnum value) {
        add(tag, value.getCode());
    }

    @Override
    public void add(int tag, IntEnum value) {
        add(tag, value.getCode());
    }

    @Override
    public void add(int tag, StringEnum value) {
        byte[] valueAsBytes = value.getBytes();
        addRaw(tag, valueAsBytes, 0, valueAsBytes.length);
    }

    @Override
    public int length() {
        return offset - start;
    }

    public int getOffset() {
        return offset;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // AppendableValue


    @Override
    public AppendableValue append(CharSequence csq) {
        if (csq != null)
            offset = CharSequenceFormatter.format(csq, buffer, offset);
        else
            append(NULL); // complies with Appendable interface
        return this;
    }

    @Override
    public AppendableValue append(CharSequence csq, int start, int end) {
        if (csq != null)
            offset = CharSequenceFormatter.format(csq, start, end, buffer, offset);
        else
            append(NULL); // complies with Appendable interface

        return this;
    }

    @Override
    public AppendableValue append(char c) {
        if ((c & 0xFFFFFF00) != 0)
            throw new IllegalArgumentException("ASCII only");
        buffer.putByte(offset++, (byte) c);
        return this;
    }

    @Override
    public AppendableValue append(byte b) {
        buffer.putByte(offset++, b);
        return this;
    }

    @Override
    public AppendableValue append(int value) {
        offset = IntFormatter.format(value, buffer, offset);
        return this;
    }

    @Override
    public AppendableValue append(int value, int minLength) {
        offset = IntFormatter.format3digits(value, buffer, offset);
        return this;
    }

    @Override
    public AppendableValue append(long value) {
        offset = LongFormatter.format(value, buffer, offset);
        return this;
    }

    @Override
    public AppendableValue append(double value) {
        offset = doubleFormatter.format(value, buffer, offset);
        return this;
    }

    @Override
    public AppendableValue append(Buffer buffer) {
        this.buffer.putBytes(offset, buffer);
        offset += buffer.capacity();
        return this;
    }

    @Override
    public AppendableValue append(Buffer buffer, int offset, int length) {
        this.buffer.putBytes(this.offset, buffer, offset, length);
        this.offset += length;
        return this;
    }

    @Override
    public AppendableValue appendTimestamp(long timestamp) {
        offset = gmtTimestampFormat.formatDateTime(timestamp, buffer, offset);
        return this;
    }

    @Override
    public void end() {
        addFieldSeparator();
    }

    protected void addTagValueSeparator() {
        buffer.putByte(offset++, Fields.TAG_VALUE_SEPARATOR);
    }

    protected void addFieldSeparator() {
        buffer.putByte(offset++, Fields.FIELD_SEPARATOR);
    }

    @Override
    public FastMessageBuilder wrap(MutableBuffer buffer, int offset, int length) {
        buffer.checkBounds(offset, length);
        this.buffer = buffer;
        this.offset = offset;
        this.start = offset;
        return this;
    }

    @Override
    public FastMessageBuilder wrap(MutableBuffer buffer) {
        this.buffer = buffer;
        this.offset = 0;
        this.start = 0;
        return this;
    }

    private static void checkValue(int tagNo, CharSequence value) {
        if (value == null)
            throw new NullPointerException("value is null for tag " + tagNo);

        if (value.length() == 0)
            throw new IllegalArgumentException("empty value for tag " + tagNo);
    }

    private static void checkValue(int tagNo, CharSequence value, int start, int end) {
        if (value == null)
            throw new NullPointerException("value is null for tag " + tagNo);

        int valueLength = value.length();
        if (start < 0 || valueLength <= start)
            throw new IllegalArgumentException("invalid start index: " + start);

        if (end < 1 || valueLength < end)
            throw new IllegalArgumentException("invalid end index: " + end);

        int length = end - start;
        if (length < 1)
            throw new IllegalArgumentException("bad length for tag " + tagNo);
    }

    private static void checkValue(int tagNo, byte[] buffer, int offset, int length) {
        if (buffer == null)
            throw new NullPointerException("value buffer is null for tag " + tagNo);

        if (offset < 0 || buffer.length <= offset)
            throw new IllegalArgumentException("invalid offset: " + offset);

        if (length < 1)
            throw new IllegalArgumentException("invalid length: " + length);

        if (offset + length > buffer.length)
            throw new IllegalArgumentException("offset + length > length of buffer");
    }

    private static void checkValue(ByteSequence value) {
        if (value == null)
            throw new NullPointerException("value is null");

        if (value.length() == 0)
            throw new IllegalArgumentException("empty value");
    }

}
