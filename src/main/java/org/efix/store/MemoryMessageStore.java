package org.efix.store;

import org.efix.util.BitUtil;
import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;

/**
 * Uses linear search from end and inserts padding in end if there is insufficient space.
 * <p>
 * Entry is aligned by 8 bytes:
 * <p>
 * |ENTRY_SIZE|SEQ_NUM|SENDING_TIME|MSG_TYPE_LENGTH|MSG_TYPE|BODY|PADDING|ENTRY_SIZE|
 * |    4     |   4   |      8     |       4       |   > 0  | > 0|  < 8  |    4     |
 */
public class MemoryMessageStore implements MessageStore {

    protected static final int NOT_FOUND = -1;

    protected static final int ALIGNMENT = BitUtil.SIZE_OF_LONG;
    protected static final int MIN_CAPACITY = 1 << 10;

    protected static final int EXTRA_SIZE = 4 * BitUtil.SIZE_OF_INT + BitUtil.SIZE_OF_LONG;

    protected final ByteSequenceWrapper msgType = new ByteSequenceWrapper();

    protected final MutableBuffer buffer;
    protected final int capacity;
    protected final int mask;

    protected final int maxEntrySize;

    protected long head;
    protected int lastSeqNum;

    public MemoryMessageStore(int capacity) {
        capacity = capacity(capacity);
        this.capacity = capacity;
        this.mask = capacity - 1;
        this.maxEntrySize = capacity >> 3;
        this.buffer = UnsafeBuffer.allocateHeap(capacity);
    }

    @Override
    public void write(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length) {
        checkSeqNum(seqNum);

        int entrySize = entrySize(msgType, length);
        int alignedSize = align(entrySize);

        int index = claim(alignedSize);
        int padding = alignedSize - entrySize;

        write(index, entrySize, padding, seqNum, time, msgType, body, offset, length);
        lastSeqNum = seqNum;
    }

    @Override
    public void read(int seqNum, Visitor visitor) {
        read(seqNum, seqNum, visitor);
    }

    @Override
    public void read(int fromSeqNum, int toSeqNum, Visitor visitor) {
        long tail = lowerBound(fromSeqNum);
        if (tail == NOT_FOUND)
            return;

        long head = this.head;

        do {
            int index = mask(tail);
            int entrySize = buffer.getInt(index);
            if (entrySize < 0) {
                tail -= entrySize;
                continue;
            }

            index += BitUtil.SIZE_OF_INT;
            int seqNum = buffer.getInt(index);

            if (seqNum <= toSeqNum) {
                index += BitUtil.SIZE_OF_INT;
                read(index, entrySize, seqNum, visitor);
            }

            if (seqNum >= toSeqNum)
                break;

            tail += align(entrySize);

        } while (tail < head);
    }

    @Override
    public void clear() {
        lastSeqNum = 0;
        head = 0;
    }

    @Override
    public void flush() {
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    protected int claim(int required) {
        int index = mask(head);
        int continuous = capacity - index;
        int padding = 0;

        if (continuous < required) {
            padding = continuous;
            int entrySize = -padding;

            buffer.putInt(index, entrySize);
            buffer.putInt(capacity - BitUtil.SIZE_OF_INT, entrySize);

            index = 0;
        }

        head += required + padding;

        return index;
    }


    protected void write(int index, int entrySize, int padding, int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length) {
        buffer.putInt(index, entrySize);
        index += BitUtil.SIZE_OF_INT;

        buffer.putInt(index, seqNum);
        index += BitUtil.SIZE_OF_INT;

        buffer.putLong(index, time);
        index += BitUtil.SIZE_OF_LONG;

        int msgTypeLength = msgType.length();

        buffer.putInt(index, msgTypeLength);
        index += BitUtil.SIZE_OF_INT;

        buffer.putBytes(index, msgType.buffer());
        index += msgTypeLength;

        buffer.putBytes(index, body, offset, length);
        index += length + padding;

        buffer.putInt(index, entrySize);
    }

    protected long lowerBound(int seqNumBound) {
        long head = this.head;
        long tail = Math.max(0, head - capacity);

        long position = NOT_FOUND;

        while (head > tail) {
            int index = mask(head - BitUtil.SIZE_OF_INT);
            int entrySize = buffer.getInt(index);

            if (entrySize < 0) {
                head += entrySize;
                continue;
            }

            head -= align(entrySize);

            if (head >= tail) {
                index = mask(head);
                int seqNum = buffer.getInt(index + BitUtil.SIZE_OF_INT);

                if (seqNum >= seqNumBound)
                    position = head;

                if (seqNum <= seqNumBound)
                    break;
            }
        }

        return position;
    }

    protected void read(int index, int entrySize, int seqNum, Visitor visitor) {
        long time = buffer.getLong(index);
        index += BitUtil.SIZE_OF_LONG;

        int msgTypeLength = buffer.getInt(index);
        index += BitUtil.SIZE_OF_INT;

        msgType.wrap(buffer, index, msgTypeLength);
        index += msgTypeLength;

        int length = bodyLength(entrySize, msgTypeLength);

        visitor.onMessage(seqNum, time, msgType, buffer, index, length);
    }

    protected int entrySize(ByteSequence msgType, int bodyLength) {
        int size = EXTRA_SIZE + msgType.length() + bodyLength;
        return checkEntrySize(size);
    }

    protected int mask(long index) {
        return (int) index & mask;
    }

    protected static int align(int length) {
        return BitUtil.align(length, ALIGNMENT);
    }

    protected static int bodyLength(int entrySize, int msgTypeLength) {
        return entrySize - EXTRA_SIZE - msgTypeLength;
    }

    protected int checkSeqNum(int seqNum) {
        if (seqNum <= lastSeqNum)
            throw new IllegalArgumentException(String.format("Seq num %s should be more previous %s", seqNum, lastSeqNum));

        return seqNum;
    }

    protected int checkEntrySize(int entrySize) {
        if (entrySize > maxEntrySize)
            throw new InsufficientSpaceException(String.format("Entry size %s exceeds max %s", entrySize, maxEntrySize));

        return entrySize;
    }

    protected static int capacity(int capacity) {
        capacity = BitUtil.nextPowerOfTwo(capacity);
        if (capacity < MIN_CAPACITY)
            throw new IllegalArgumentException(String.format("Capacity %s < %s", capacity, MIN_CAPACITY));

        return capacity;
    }

}
