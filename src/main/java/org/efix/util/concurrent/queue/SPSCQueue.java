package org.efix.util.concurrent.queue;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class SPSCQueue<E> extends AbstractQueue<E> {

    public SPSCQueue(int requestedCapacity) {
        super(requestedCapacity);
    }

    @Override
    public boolean offer(E e) {
        requireNonNull(e);

        long head = headSequence.get();
        long tail = tailSequenceCache.get();
        long limit = tail + capacity;
        if (head >= limit) {
            tail = tailSequence.getVolatile();
            limit = tail + capacity;
            if (head >= limit)
                return false;

            tailSequenceCache.set(tail);
        }

        array.setOrdered(mask(head), e);
        headSequence.setOrdered(head + 1);

        return true;
    }

    @Override
    public int drain(Consumer<E> handler) {
        int read = 0;
        long tail = tailSequence.get();
        long head = headSequence.getVolatile();
        int available = (int) (head - tail);
        while (read < available) {
            int index = mask(tail + read);
            E e = array.get(index);

            read++;

            array.set(index, null);
            tailSequence.setOrdered(tail + read);

            handler.accept(e);
        }

        return read;
    }

}
