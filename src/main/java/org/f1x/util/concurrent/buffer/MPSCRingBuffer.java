package org.f1x.util.concurrent.buffer;

import org.f1x.util.buffer.AtomicBuffer;
import org.f1x.util.buffer.Buffer;


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
        long tail = tailSequence.get();

        int bytesRead = 0;

        try {
            while (bytesRead < capacity) {
                int recordOffset = mask(tail + bytesRead);
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
                int tailIndex = mask(tail);
                int continuous = capacity - tailIndex;

                if (continuous >= bytesRead) {
                    buffer.setMemory(tailIndex, bytesRead, (byte) 0);
                } else {
                    buffer.setMemory(tailIndex, continuous, (byte) 0);
                    buffer.setMemory(0, bytesRead - continuous, (byte) 0);
                }

                tailSequence.setOrdered(tail + bytesRead);
            }
        }

        return messagesRead;
    }

    private int claim(int required) {
        long tail = tailCacheSequence.getVolatile();
        long head;
        int headIndex;
        int padding;

        do {
            head = headSequence.getVolatile();

            if (required > freeSpace(head, tail, capacity)) {
                tail = tailSequence.getVolatile();
                if (required > freeSpace(head, tail, capacity))
                    return INSUFFICIENT_SPACE;

                tailCacheSequence.setOrdered(tail);
            }

            padding = 0;
            headIndex = mask(head);
            int continuous = capacity - headIndex;

            if (required > continuous) {
                if (required > mask(tail)) {
                    tail = tailSequence.getVolatile();
                    if (required > mask(tail))
                        return INSUFFICIENT_SPACE;

                    tailCacheSequence.setOrdered(tail);
                }

                padding = continuous;
            }
        } while (!headSequence.compareAndSet(head, head + required + padding));

        if (padding != 0) {
            buffer.putInt(messageTypeOffset(headIndex), MESSAGE_TYPE_PADDING);
            buffer.putIntOrdered(recordLengthOffset(headIndex), padding);
            headIndex = 0;
        }

        return headIndex;
    }

}
