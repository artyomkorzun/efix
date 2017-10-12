package org.efix.message;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.efix.util.parse.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public final class Message {

    private final static int NOT_FOUND = -1;

    private final MutableBuffer buffer = new UnsafeBuffer(0, 0);
    private final float loadFactor;

    private int[] entries;
    private int size;
    private int resizeThreshold;


    public Message() {
        this(128);
    }

    public Message(int initialCapacity) {
        this(initialCapacity, 0.55f);
    }

    public Message(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        resize(initialCapacity);
    }

    public Buffer buffer() {
        return buffer;
    }

    public void parse(Buffer buffer, int offset, int length) {
        this.buffer.wrap(buffer, offset, length);
        clear();

        buffer = this.buffer;
        offset = 0;

        do {
            int tag = buffer.getByte(offset++) - '0';

            while (true) {
                byte b = buffer.getByte(offset++);
                if (b == '=') {
                    break;
                }

                tag = 10 * tag + (b - '0');
            }

            int valueOffset = offset;
            while (buffer.getByte(++offset) != '\u0001') ;

            put(tag, valueOffset, offset++);
        } while (offset < length);
    }

    public boolean getBool(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return BoolParser.parseBool(tag, buffer, offset, end);
    }

    public boolean getBool(int tag, boolean noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return BoolParser.parseBool(tag, buffer, offset, end);
    }

    public byte getByte(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return ByteParser.parseByte(tag, buffer, offset, end);
    }

    public byte getByte(int tag, byte noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return ByteParser.parseByte(tag, buffer, offset, end);
    }

    public int getInt(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return IntParser.parseInt(tag, buffer, offset, end);
    }

    public int getInt(int tag, int noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return IntParser.parseInt(tag, buffer, offset, end);
    }

    public int getUInt(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return IntParser.parseUInt(tag, buffer, offset, end);
    }

    public int getUInt(int tag, int noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return IntParser.parseUInt(tag, buffer, offset, end);
    }

    public long getLong(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return LongParser.parseLong(tag, buffer, offset, end);
    }

    public long getLong(int tag, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return LongParser.parseLong(tag, buffer, offset, end);
    }

    public long getULong(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return LongParser.parseULong(tag, buffer, offset, end);
    }

    public long getULong(int tag, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return LongParser.parseULong(tag, buffer, offset, end);
    }

    public double getDouble(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DoubleParser.parseDouble(tag, buffer, offset, end);
    }

    public double getDouble(int tag, double noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DoubleParser.parseDouble(tag, buffer, offset, end);
    }

    public double getUDouble(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DoubleParser.parseUDouble(tag, buffer, offset, end);
    }

    public double getUDouble(int tag, double noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DoubleParser.parseUDouble(tag, buffer, offset, end);
    }

    public long getDecimal(int tag, int scale) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseDecimal(tag, scale, buffer, offset, end);
    }

    public long getDecimal(int tag, int scale, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseDecimal(tag, scale, buffer, offset, end);
    }

    public long getUDecimal(int tag, int scale) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseUDecimal(tag, scale, buffer, offset, end);
    }

    public long getUDecimal(int tag, int scale, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseUDecimal(tag, scale, buffer, offset, end);
    }

    public long getDecimalHalfUp(int tag, int scale) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseDecimalHalfUp(tag, scale, buffer, offset, end);
    }

    public long getDecimalHalfUp(int tag, int scale, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseDecimalHalfUp(tag, scale, buffer, offset, end);
    }

    public long getUDecimalHalfUp(int tag, int scale) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseUDecimalHalfUp(tag, scale, buffer, offset, end);
    }

    public long getUDecimalHalfUp(int tag, int scale, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DecimalParser.parseUDecimalHalfUp(tag, scale, buffer, offset, end);
    }

    public long getTimestampMs(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimestampParser.parseTimestampMs(tag, buffer, offset, end);
    }

    public long getTimestampMs(int tag, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimestampParser.parseTimestampMs(tag, buffer, offset, end);
    }

    public long getTimestampNs(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimestampParser.parseTimestampNs(tag, buffer, offset, end);
    }

    public long getTimestampNs(int tag, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimestampParser.parseTimestampNs(tag, buffer, offset, end);
    }

    public long getDate(int tag/*, TimeUnit units*/) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DateParser.parseDate(tag, buffer, offset, end);
    }

    public long getDate(int tag/*, TimeUnit units*/, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return DateParser.parseDate(tag, buffer, offset, end);
    }

    public long getTime(int tag/*, TimeUnit units*/) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimeParser.parseTime(tag, buffer, offset, end);
    }

    public long getTime(int tag/*, TimeUnit units*/, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimeParser.parseTime(tag, buffer, offset, end);
    }

    public String getString(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        int length = end - offset;
        byte[] array = new byte[length];

        MutableBuffer buffer = this.buffer;
        buffer.getBytes(offset, array);

        return new String(array, StandardCharsets.US_ASCII);
    }

    public String getString(int tag, String noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        int length = end - offset;
        byte[] array = new byte[length];

        MutableBuffer buffer = this.buffer;
        buffer.getBytes(offset, array);

        return new String(array, StandardCharsets.US_ASCII);
    }

    public ByteSequence getString(int tag, ByteSequenceWrapper wrapper) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        wrapper.wrap(buffer, offset, end - offset);
        return wrapper;
    }

    public ByteSequence getString(int tag, ByteSequenceWrapper wrapper, ByteSequence noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        wrapper.wrap(buffer, offset, end - offset);
        return wrapper;
    }

    public Buffer getRaw(int tag, MutableBuffer wrapper) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        wrapper.wrap(buffer, offset, end - offset);
        return wrapper;
    }

    public Buffer getRaw(int tag, MutableBuffer wrapper, Buffer noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        wrapper.wrap(buffer, offset, end - offset);
        return wrapper;
    }

    public void getAny(int tag) throws NoFieldException {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            throw new NoFieldException(tag);
        }
    }

    public boolean has(int tag) {
        final int[] entries = this.entries;
        return getIndex(tag, entries) != NOT_FOUND;
    }

    public void clear() {
        Arrays.fill(entries, NOT_FOUND);
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void rehash(final int newCapacity) {
        final int[] entries = this.entries;
        final int length = this.entries.length;

        resize(newCapacity);

        for (int index = 0; index < length; index += 4) {
            if (entries[index] != NOT_FOUND) {
                put(entries[index + 0], entries[index + 1], entries[index + 2]);
            }
        }
    }

    private void resize(final int newCapacity) {
        final int newLength = newCapacity * 4;
        resizeThreshold = (int) (newCapacity * loadFactor);
        entries = new int[newLength];
        size = 0;
        Arrays.fill(entries, NOT_FOUND);
    }

    private void put(int tag, int offset, int end) {
        final int[] entries = this.entries;
        final int mask = entries.length - 1;

        int index = (tag << 2) & mask;

        while (entries[index] != NOT_FOUND) {
            if (tag == entries[index]) {
                return;
            }

            index = next(index, mask);
        }

        entries[index + 0] = tag;
        entries[index + 1] = offset;
        entries[index + 2] = end;

        if (++size > resizeThreshold) {
            // entries.length = 4 * capacity
            final int newCapacity = entries.length >>> 1;
            rehash(newCapacity);
        }
    }

    private static int getIndex(int tag, int[] entries) {
        final int mask = entries.length - 1;

        int index = (tag << 2) & mask;

        while (entries[index] != NOT_FOUND) {
            if (tag == entries[index]) {
                return index;
            }

            index = next(index, mask);
        }

        return NOT_FOUND;
    }

    private static int next(final int index, final int mask) {
        return (index + 4) & mask;
    }

    @Override
    public String toString() {
        return new ByteSequenceWrapper(buffer).toString();
    }

}
