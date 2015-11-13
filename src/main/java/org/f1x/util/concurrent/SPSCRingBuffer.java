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

        int recordLength = recordLength(length);
        int alignedRecordLength = align(recordLength);
        long head = claim(alignedRecordLength);

        if (head == INSUFFICIENT_SPACE)
            return false;

        int recordOffset = mask(head);
        putHeader(recordOffset, recordLength, messageType, buffer);
        buffer.putBytes(messageOffset(recordOffset), srcBuffer, srcOffset, length);
        headSequence.setOrdered(head + alignedRecordLength);

        return true;
    }

    @Override
    public int read(MessageHandler handler) {
        int messagesRead = 0;

        long tail = tailSequence.get();
        long head = headSequence.getVolatile();

        int available = (int) (head - tail);
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

        return messagesRead;
    }

    private long claim(int required) {
        long head = headSequence.get();
        long tail = tailCacheSequence.get();

        if (required > freeSpace(head, tail, capacity)) {
            tail = tailSequence.getVolatile();
            if (required > freeSpace(head, tail, capacity))
                return INSUFFICIENT_SPACE;

            tailCacheSequence.set(tail);
        }

        int padding = 0;
        int headIndex = mask(head);
        int continuous = capacity - headIndex;

        if (required > continuous) {
            if (required > mask(tail)) {
                tail = tailSequence.getVolatile();
                if (required > mask(tail))
                    return INSUFFICIENT_SPACE;

                tailCacheSequence.set(tail);
            }

            padding = continuous;
            putHeader(headIndex, padding, MESSAGE_TYPE_PADDING, buffer);
            headSequence.setOrdered(head + padding);
        }

        return head + padding;
    }

    private static void putHeader(int recordOffset, int recordLength, int messageType, AtomicBuffer buffer) {
        buffer.putInt(recordLengthOffset(recordOffset), recordLength);
        buffer.putInt(messageTypeOffset(recordOffset), messageType);
    }

}
