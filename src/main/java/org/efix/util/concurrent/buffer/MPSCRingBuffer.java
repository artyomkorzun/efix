package org.efix.util.concurrent.buffer;

import org.efix.util.buffer.AtomicBuffer;
import org.efix.util.buffer.Buffer;


public class MPSCRingBuffer extends AbstractRingBuffer implements RingBuffer {

    public MPSCRingBuffer(int capacity) {
        super(capacity);
    }

    @Override
    public boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length) {
        checkMessageType(messageType);
        checkMessageLength(length);

        int recordLength = recordLength(length);
        int alignedRecordLength = align(recordLength);
        int recordOffset = claim(alignedRecordLength);

        if (recordOffset == INSUFFICIENT_SPACE)
            return false;

        buffer.putInt(messageTypeOffset(recordOffset), messageType);
        buffer.putBytes(messageOffset(recordOffset), srcBuffer, srcOffset, length);
        buffer.putIntOrdered(recordLengthOffset(recordOffset), recordLength);

        return true;
    }

    @Override
    public int read(MessageHandler handler) {
        int messagesRead = 0;

        AtomicBuffer buffer = this.buffer;
        int capacity = this.capacity;
        long head = headSequence.get();

        int bytesRead = 0;

        try {
            while (bytesRead < capacity) {
                int recordOffset = mask(head + bytesRead);
                int recordLength = buffer.getIntVolatile(recordLengthOffset(recordOffset));
                if (recordLength == 0)
                    break;

                bytesRead += align(recordLength);

                int messageType = buffer.getInt(messageTypeOffset(recordOffset));
                if (messageType == MESSAGE_TYPE_PADDING)
                    continue;

                handler.onMessage(messageType, buffer, messageOffset(recordOffset), messageLength(recordLength));
                messagesRead++;
            }
        } finally {
            if (bytesRead > 0) {
                int headIndex = mask(head);
                int continuous = capacity - headIndex;

                if (continuous >= bytesRead) {
                    buffer.setMemory(headIndex, bytesRead, (byte) 0);
                } else {
                    buffer.setMemory(headIndex, continuous, (byte) 0);
                    buffer.setMemory(0, bytesRead - continuous, (byte) 0);
                }

                headSequence.setOrdered(head + bytesRead);
            }
        }

        return messagesRead;
    }

    private int claim(int required) {
        long head = headCacheSequence.getVolatile();
        long tail;
        int tailIndex;
        int padding;

        do {
            tail = tailSequence.getVolatile();

            if (required > freeSpace(head, tail, capacity)) {
                head = headSequence.getVolatile();
                if (required > freeSpace(head, tail, capacity))
                    return INSUFFICIENT_SPACE;

                headCacheSequence.setOrdered(head);
            }

            padding = 0;
            tailIndex = mask(tail);
            int continuous = capacity - tailIndex;

            if (required > continuous) {
                if (required > mask(head)) {
                    head = headSequence.getVolatile();
                    if (required > mask(head))
                        return INSUFFICIENT_SPACE;

                    headCacheSequence.setOrdered(head);
                }

                padding = continuous;
            }
        } while (!tailSequence.compareAndSet(tail, tail + required + padding));

        if (padding != 0) {
            buffer.putInt(messageTypeOffset(tailIndex), MESSAGE_TYPE_PADDING);
            buffer.putIntOrdered(recordLengthOffset(tailIndex), padding);
            tailIndex = 0;
        }

        return tailIndex;
    }

}
