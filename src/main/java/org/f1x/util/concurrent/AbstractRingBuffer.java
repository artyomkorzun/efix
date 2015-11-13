package org.f1x.util.concurrent;

import org.f1x.util.BitUtil;
import org.f1x.util.buffer.AtomicBuffer;

public abstract class AbstractRingBuffer implements RingBuffer {

    protected static final int MESSAGE_TYPE_PADDING = -1;
    protected static final int INSUFFICIENT_SPACE = -2;

    protected static final int MIN_CAPACITY = 1024;
    protected static final int ALIGNMENT = BitUtil.SIZE_OF_LONG;

    protected static final int HEADER_LENGTH = 2 * BitUtil.SIZE_OF_INT;

    protected final Sequence headSequence = new Sequence();
    protected final Sequence tailSequence = new Sequence();
    protected final AtomicBuffer buffer;
    protected final int capacity;
    protected final int mask;
    protected final int maxMessageLength;
    protected final Sequence tailCacheSequence = new Sequence();

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
    public int size() {
        long tail = tailSequence.getVolatile();
        long head = headSequence.getVolatile();
        return (int) (head - tail);
    }

    protected static int recordLength(int messageLength) {
        return messageLength + HEADER_LENGTH;
    }

    protected static int messageTypeOffset(int recordOffset) {
        return recordOffset + BitUtil.SIZE_OF_INT;
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

    protected static int freeSpace(long head, long tail, int capacity) {
        return capacity - (int) (head - tail);
    }

    protected int mask(long index) {
        return mask((int) index);
    }

    protected int mask(int index) {
        return index & mask;
    }

    protected static int align(int length) {
        return BitUtil.align(length, ALIGNMENT);
    }

    protected void checkMessageLength(int length) {
        if (length > maxMessageLength)
            throw new IllegalArgumentException(String.format("Message length %s is more than max %d", length, maxMessageLength));
    }

    protected void checkMessageType(int messageType) {
        if (messageType < 1)
            throw new IllegalArgumentException(String.format("Message type: %s is non positive", messageType));
    }

    protected static AtomicBuffer checkBuffer(AtomicBuffer buffer) {
        int capacity = buffer.capacity();
        if (!BitUtil.isPowerOfTwo(capacity) || capacity < MIN_CAPACITY)
            throw new IllegalArgumentException(String.format("Capacity: %s must be a power of 2 and not less: %s", capacity, MIN_CAPACITY));

        buffer.checkAlignment();
        return buffer;
    }

}
