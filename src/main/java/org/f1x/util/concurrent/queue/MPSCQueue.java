package org.f1x.util.concurrent.queue;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class MPSCQueue<E> extends AbstractQueue<E> {

    public MPSCQueue(int requestedCapacity) {
        super(requestedCapacity);
    }

    @Override
    public boolean offer(E e) {
        requireNonNull(e);

        long head;
        long tail = tailCacheSequence.getVolatile();
        long limit = tail + capacity;
        do {
            head = headSequence.getVolatile();
            if (head >= limit) {
                tail = tailSequence.getVolatile();
                limit = tail + capacity;
                if (head >= limit)
                    return false;

                tailCacheSequence.setOrdered(tail);
            }
        } while (!headSequence.compareAndSwap(head, head + 1));

        array.setOrderedObject(mask(head), e);

        return true;
    }

    @Override
    public int drain(Consumer<E> handler) {
        int readMessages = 0;
        long tail = tailSequence.get();
        while (readMessages < capacity) {
            int index = mask(tail + readMessages);
            E e = array.getObjectVolatile(index);
            if (e == null)
                break;

            readMessages++;

            array.setObject(index, null);
            tailSequence.setOrdered(tail + readMessages);

            handler.accept(e);
        }

        return readMessages;
    }

}
