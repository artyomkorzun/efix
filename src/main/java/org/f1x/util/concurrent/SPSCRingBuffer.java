package org.f1x.util.concurrent;


import org.f1x.util.buffer.AtomicBuffer;

/**
 * Single producer with single consumer.
 */
public class SPSCRingBuffer extends AbstractRingBuffer implements RingBuffer {

    public SPSCRingBuffer(AtomicBuffer buffer) {
        super(buffer);
    }

    @Override
    protected int claim(int recordLength) {
        int required = align(recordLength);
        long head = headSequence.get();
        long tail = tailSequence.getVolatile();
        int free = capacity - (int) (head - tail);
        if (required > free)
            return INSUFFICIENT_CAPACITY;

        int headIndex = mask(head);
        int tailIndex = mask(tail);
        int continuous = capacity - headIndex;

        if (required > continuous) {
            if (required > tailIndex)
                return INSUFFICIENT_CAPACITY;

            buffer.putInt(recordLengthOffset(headIndex), continuous);
            buffer.putInt(messageTypeOffset(headIndex), MESSAGE_TYPE_PADDING);
            headSequence.setOrdered(head + continuous);
            headIndex = 0;
        }

        return headIndex;
    }

    @Override
    protected void publish(int recordOffset, int recordLength) {
        buffer.putInt(recordLengthOffset(recordOffset), recordLength);
        long head = headSequence.get();
        headSequence.setOrdered(head + align(recordLength));
    }

    @Override
    public int read(MessageHandler handler, int messagesLimit) {
        int messagesRead = 0;

        long tail = tailSequence.get();
        long head = headSequence.getVolatile();
        int available = (int) (head - tail);

        if (available > 0) {
            int bytesRead = 0;
            int tailIndex = mask(tail);

            try {
                while (bytesRead < available && messagesRead < messagesLimit) {
                    int recordOffset = mask(tailIndex + bytesRead);
                    int recordLength = buffer.getInt(recordLengthOffset(recordOffset));
                    int messageType = buffer.getInt(messageTypeOffset(recordOffset));

                    if (messageType != MESSAGE_TYPE_PADDING) {
                        if (!handler.onMessage(messageType, buffer, messageOffset(recordOffset), messageLength(recordLength)))
                            break;

                        messagesRead++;
                    }

                    bytesRead += align(recordLength);
                }
            } finally {
                tailSequence.setOrdered(tail + bytesRead);
            }
        }

        return messagesRead;
    }

}
