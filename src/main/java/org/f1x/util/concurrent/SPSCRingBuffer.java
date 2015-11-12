package org.f1x.util.concurrent;

import org.f1x.util.buffer.AtomicBuffer;
import org.f1x.util.buffer.Buffer;

public class SPSCRingBuffer extends AbstractRingBuffer implements RingBuffer {

    public SPSCRingBuffer(AtomicBuffer buffer) {
        super(buffer);
    }

    @Override
    public boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length) {
        checkMessageType(messageType);
        checkMessageLength(length);

        AtomicBuffer buffer = this.buffer;
        int recordLength = recordLength(length);
        int alignedRecordLength = align(recordLength);

        long head = headSequence.get();
        int recordOffset = claim(alignedRecordLength, head, buffer);

        if (recordOffset == INSUFFICIENT_SPACE)
            return false;

        buffer.putInt(recordLengthOffset(recordOffset), recordLength);
        buffer.putInt(messageTypeOffset(recordOffset), messageType);
        buffer.putBytes(messageOffset(recordOffset), srcBuffer, srcOffset, length);

        headSequence.setOrdered(head + alignedRecordLength);

        return true;
    }

    @Override
    public int read(MessageHandler handler) {
        int messagesRead = 0;

        AtomicBuffer buffer = this.buffer;

        long tail = tailSequence.get();
        long head = headSequence.getVolatile();

        int available = (int) (head - tail);

        if (available > 0) {
            int bytesRead = 0;

            while (bytesRead < available) {
                int recordOffset = mask(tail + bytesRead);
                int recordLength = buffer.getInt(recordLengthOffset(recordOffset));
                int messageType = buffer.getInt(messageTypeOffset(recordOffset));

                bytesRead += align(recordLength);

                try {
                    if (messageType != MESSAGE_TYPE_PADDING) {
                        handler.onMessage(messageType, buffer, messageOffset(recordOffset), messageLength(recordLength));
                        messagesRead++;
                    }
                } finally {
                    tailSequence.setOrdered(tail + bytesRead);
                }
            }
        }

        return messagesRead;
    }

    private int claim(int required, long head, AtomicBuffer buffer) {
        int capacity = this.capacity;
        long tail = tailCacheSequence.get();

        if (required > freeSpace(head, tail, capacity)) {
            tail = tailSequence.getVolatile();
            if (required > freeSpace(head, tail, capacity))
                return INSUFFICIENT_SPACE;

            tailCacheSequence.set(tail);
        }

        int headIndex = mask(head);
        int tailIndex = mask(tail);
        int continuous = capacity - headIndex;

        if (required > continuous) {
            if (required > tailIndex)
                return INSUFFICIENT_SPACE;

            buffer.putInt(recordLengthOffset(headIndex), continuous);
            buffer.putInt(messageTypeOffset(headIndex), MESSAGE_TYPE_PADDING);
            headSequence.setOrdered(head + continuous);
            headIndex = 0;
        }

        return headIndex;
    }

}
