package org.efix.util.concurrent.buffer;

import org.efix.util.buffer.AtomicBuffer;
import org.efix.util.buffer.Buffer;


public class SPSCRingBuffer extends AbstractRingBuffer implements RingBuffer {

    public SPSCRingBuffer(int capacity) {
        super(capacity);
    }

    @Override
    public boolean write(int messageType, Buffer srcBuffer, int srcOffset, int length) {
        checkMessageType(messageType);
        checkMessageLength(length);

        int recordLength = recordLength(length);
        int alignedRecordLength = align(recordLength);
        long tail = claim(alignedRecordLength);

        if (tail == INSUFFICIENT_SPACE)
            return false;

        int recordOffset = mask(tail);
        putHeader(recordOffset, recordLength, messageType, buffer);
        buffer.putBytes(messageOffset(recordOffset), srcBuffer, srcOffset, length);
        tailSequence.setOrdered(tail + alignedRecordLength);

        return true;
    }

    @Override
    public int read(MessageHandler handler) {
        int messagesRead = 0;

        AtomicBuffer buffer = this.buffer;
        long head = headSequence.get();
        long tail = tailSequence.getVolatile();

        int available = (int) (tail - head);
        int bytesRead = 0;

        try {
            while (bytesRead < available) {
                int recordOffset = mask(head + bytesRead);
                int recordLength = buffer.getInt(recordLengthOffset(recordOffset));
                int messageType = buffer.getInt(messageTypeOffset(recordOffset));

                bytesRead += align(recordLength);

                if (messageType == MESSAGE_TYPE_PADDING)
                    continue;

                handler.onMessage(messageType, buffer, messageOffset(recordOffset), messageLength(recordLength));
                messagesRead++;
            }
        } finally {
            headSequence.setOrdered(head + bytesRead);
        }

        return messagesRead;
    }

    private long claim(int required) {
        long tail = tailSequence.get();
        long head = headCacheSequence.get();

        if (required > freeSpace(head, tail, capacity)) {
            head = headSequence.getVolatile();
            if (required > freeSpace(head, tail, capacity))
                return INSUFFICIENT_SPACE;

            headCacheSequence.set(head);
        }

        int padding = 0;
        int tailIndex = mask(tail);
        int continuous = capacity - tailIndex;

        if (required > continuous) {
            if (required > mask(head)) {
                head = headSequence.getVolatile();
                if (required > mask(head))
                    return INSUFFICIENT_SPACE;

                headCacheSequence.set(head);
            }

            padding = continuous;
            putHeader(tailIndex, padding, MESSAGE_TYPE_PADDING, buffer);
        }

        return tail + padding;
    }

    private static void putHeader(int recordOffset, int recordLength, int messageType, AtomicBuffer buffer) {
        buffer.putInt(recordLengthOffset(recordOffset), recordLength);
        buffer.putInt(messageTypeOffset(recordOffset), messageType);
    }

}
