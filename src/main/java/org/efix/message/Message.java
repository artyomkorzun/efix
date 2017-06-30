package org.efix.message;

import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;
import org.efix.util.parse.*;

import java.util.Arrays;


public final class Message {

    private final static int NOT_FOUND = -1;

    private final float loadFactor;

    private int[] entries;
    private int size;
    private int resizeThreshold;

    private Buffer buffer;

    public Message() {
        this(128);
    }

    public Message(int initialCapacity) {
        this(initialCapacity, 0.55f);
    }

    public Message(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        capacity(initialCapacity);
    }

    public void parse(Buffer buffer, int offset, int length) {
        this.buffer = buffer;
        clear();

        final int end = offset + length;

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
        } while (offset < end);
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

    public long getTimestamp(int tag/*, TimeUnit units*/, long noValue) {
        final int[] entries = this.entries;
        final int index = getIndex(tag, entries);

        if (index == NOT_FOUND) {
            return noValue;
        }

        int offset = entries[index + 1];
        int end = entries[index + 2];

        return TimestampParser.parseTimestamp(tag, buffer, offset, end);
    }

   /* public long getDate(int tag, long noValue) {
        return noValue; // TODO
    }

    public int getTime(int tag, int noValue) {
        return noValue; // TODO
    }*/

    public CharSequence getString(int tag, ByteSequenceWrapper wrapper, CharSequence noValue) {
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

    public boolean contains(int tag) {
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

        capacity(newCapacity);

        for (int index = 0; index < length; index += 4) {
            if (entries[index] != NOT_FOUND) {
                put(entries[index + 0], entries[index + 1], entries[index + 2]);
            }
        }
    }

    private void capacity(final int newCapacity) {
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

}