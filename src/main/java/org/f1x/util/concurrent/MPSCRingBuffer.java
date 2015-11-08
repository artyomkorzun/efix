package org.f1x.util.concurrent;


import org.f1x.util.buffer.AtomicBuffer;

/**
 * Many producers with one consumer.
 */
public class MPSCRingBuffer extends AbstractRingBuffer implements RingBuffer {

    public MPSCRingBuffer(AtomicBuffer buffer) {
        super(buffer);
    }

    protected int claim(int recordLength) {
        int required = align(recordLength);
        long tail = tailSequence.getVolatile();
        int tailIndex = mask(tail);

        long head;
        int headIndex;
        int padding;

        do {
            head = headSequence.getVolatile();
            int free = capacity - (int) (head - tail);

            if (required > free)
                return INSUFFICIENT_CAPACITY;

            padding = 0;
            headIndex = mask(head);
            int continuous = capacity - headIndex;

            if (required > continuous) {
                if (required > tailIndex)
                    return INSUFFICIENT_CAPACITY;

                padding = continuous;
            }
        }
        while (!headSequence.compareAndSwap(head, head + required + padding));

        if (padding != 0) {
            buffer.putInt(messageTypeOffset(headIndex), MESSAGE_TYPE_PADDING);
            buffer.putIntOrdered(recordLengthOffset(headIndex), padding);
            headIndex = 0;
        }

        return headIndex;
    }

    @Override
    protected void publish(int recordOffset, int recordLength) {
        buffer.putIntOrdered(recordLengthOffset(recordOffset), recordLength);
    }

    @Override
    public int read(Reader reader, int messagesLimit) {
        int messagesRead = 0;

        long tail = tailSequence.get();
        long head = headSequence.getVolatile();
        int available = (int) (head - tail);

        if (available > 0) {
            int bytesRead = 0;

            int paddingOffset = -1;
            int tailIndex = mask(tail);

            try {
                while (bytesRead < available && messagesRead < messagesLimit) {
                    int recordOffset = mask(tailIndex + bytesRead);
                    int recordLength = buffer.getIntVolatile(recordLengthOffset(recordOffset));
                    if (recordLength <= 0)
                        break;

                    int messageType = buffer.getInt(messageTypeOffset(recordOffset));
                    if (messageType == MESSAGE_TYPE_PADDING) {
                        paddingOffset = recordOffset;
                    } else {
                        if (!reader.read(messageType, buffer, messageOffset(recordOffset), messageLength(recordLength)))
                            break;

                        messagesRead++;
                    }

                    bytesRead += align(recordLength);
                }
            } finally {
                if (paddingOffset == -1) {
                    buffer.setMemory(tailIndex, bytesRead, (byte) 0);
                } else {
                    buffer.setMemory(tailIndex, messageOffset(paddingOffset) - tailIndex, (byte) 0);
                    buffer.setMemory(0, bytesRead - (capacity - tailIndex), (byte) 0);
                }

                tailSequence.setOrdered(tail + bytesRead);
            }
        }

        return messagesRead;
    }

}
