package org.f1x.util.concurrent;

import org.f1x.util.Bits;
import org.f1x.util.buffer.AtomicBuffer;
import org.f1x.util.buffer.Buffer;

public abstract class AbstractRingBuffer implements RingBuffer {

    protected static final int INSUFFICIENT_CAPACITY = -1;

    protected static final int MIN_CAPACITY = 1024;
    protected static final int ALIGNMENT = Bits.SIZE_OF_LONG;

    protected static final int HEADER_LENGTH = 2 * Bits.SIZE_OF_INT;

    protected static final int MESSAGE_TYPE_PADDING = -1;

    protected final Sequence headSequence = new Sequence(0);
    protected final Sequence tailSequence = new Sequence(0);
    protected final AtomicBuffer buffer;
    protected final int capacity;
    protected final int mask;
    protected final int maxMessageLength;

    public AbstractRingBuffer(AtomicBuffer buffer) {
        this.buffer = checkBuffer(buffer);
        this.capacity = buffer.capacity();
        this.mask = capacity - 1;
        this.maxMessageLength = capacity >>> 3;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int maxMessageLength() {
        return maxMessageLength;
    }

    @Override
    public boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length) {
        checkMessageType(messageType);
        checkMessageLength(length);

        int recordLength = recordLength(length);
        int recordOffset = claim(recordLength);

        if (recordOffset == INSUFFICIENT_CAPACITY)
            return false;

        try {
            buffer.putInt(messageTypeOffset(recordOffset), messageType);
            buffer.putBytes(messageOffset(recordOffset), srcBuffer, srcOffset, length);
        } finally {
            publish(recordOffset, recordLength);
        }

        return true;
    }

    @Override
    public boolean write(int messageType, Writer writer, int length) {
        checkMessageType(messageType);
        checkMessageLength(length);

        int recordLength = recordLength(length);
        int recordOffset = claim(recordLength);

        if (recordOffset == INSUFFICIENT_CAPACITY)
            return false;

        try {
            buffer.putInt(messageTypeOffset(recordOffset), messageType);
            writer.write(buffer, messageOffset(recordOffset), length);
        } finally {
            publish(recordOffset, recordLength);
        }

        return true;
    }

    @Override
    public int read(Reader reader) {
        return read(reader, Integer.MAX_VALUE);
    }

    protected abstract int claim(int recordLength);

    protected abstract void publish(int recordOffset, int recordLength);

    protected static int recordLength(int messageLength) {
        return messageLength + HEADER_LENGTH;
    }

    protected static int messageTypeOffset(int recordOffset) {
        return recordOffset + Bits.SIZE_OF_INT;
    }

    protected static int recordLengthOffset(int recordOffset) {
        return recordOffset;
    }

    protected static int messageOffset(int recordOffset) {
        return recordOffset + HEADER_LENGTH;
    }

    protected static int messageLength(int recordLength) {
        return recordLength - HEADER_LENGTH;
    }

    protected int mask(long index) {
        return mask((int) index);
    }

    protected int mask(int index) {
        return index & mask;
    }

    protected static int align(int length) {
        return Bits.align(length, ALIGNMENT);
    }

    protected void checkMessageLength(int length) {
        if (length > maxMessageLength)
            throw new IllegalArgumentException(String.format("Message length: %s is more than max: %d", length, maxMessageLength));
    }

    protected void checkMessageType(int messageType) {
        if (messageType < 1)
            throw new IllegalArgumentException(String.format("Message type: %s is non positive", messageType));
    }

    protected static AtomicBuffer checkBuffer(AtomicBuffer buffer) {
        int capacity = buffer.capacity();
        if (!Bits.isPowerOfTwo(capacity) || capacity < MIN_CAPACITY)
            throw new IllegalArgumentException(String.format("Capacity: %s must be a power of 2 and not less: %s", capacity, MIN_CAPACITY));

        buffer.verifyAlignment();
        return buffer;
    }

}
