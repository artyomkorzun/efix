package org.f1x.util.concurrent.queue;

import java.util.Objects;
import java.util.function.Consumer;

public class SPSCQueue<E> extends AbstractQueue<E> {

    public SPSCQueue(int requestedCapacity) {
        super(requestedCapacity);
    }

    @Override
    public boolean offer(E e) {
        Objects.requireNonNull(e);

        long head = headSequence.get();
        long tail = tailCacheSequence.get();
        long limit = tail + capacity;
        if (head >= limit) {
            tail = tailSequence.getVolatile();
            limit = tail + capacity;
            if (head >= limit)
                return false;

            tailCacheSequence.set(tail);
        }

        array.setOrderedObject(mask(head), e);
        headSequence.setOrdered(head + 1);

        return true;
    }

    @Override
    public int drain(Consumer<E> handler) {
        int readMessages = 0;
        long tail = tailSequence.get();
        long head = headSequence.getVolatile();
        int available = (int) (head - tail);
        while (readMessages < available) {
            int index = mask(tail + readMessages);
            E e = array.getObject(index);

            readMessages++;

            array.setObject(index, null);
            tailSequence.setOrdered(tail + readMessages);

            handler.accept(e);
        }

        return readMessages;
    }

}
