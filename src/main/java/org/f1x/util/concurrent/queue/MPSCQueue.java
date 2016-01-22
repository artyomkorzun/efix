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
        long tail = tailSequenceCache.getVolatile();
        long limit = tail + capacity;
        do {
            head = headSequence.getVolatile();
            if (head >= limit) {
                tail = tailSequence.getVolatile();
                limit = tail + capacity;
                if (head >= limit)
                    return false;

                tailSequenceCache.setOrdered(tail);
            }
        } while (!headSequence.compareAndSet(head, head + 1));

        array.setOrdered(mask(head), e);

        return true;
    }

    @Override
    public int drain(Consumer<E> handler) {
        int read = 0;
        long tail = tailSequence.get();
        while (read < capacity) {
            int index = mask(tail + read);
            E e = array.getVolatile(index);
            if (e == null)
                break;

            read++;

            array.set(index, null);
            tailSequence.setOrdered(tail + read);

            handler.accept(e);
        }

        return read;
    }

}
