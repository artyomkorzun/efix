package org.f1x.util.concurrent;

import java.util.Objects;
import java.util.function.Consumer;

public class SPSCPipe<E> extends AbstractPipe<E> {

    public SPSCPipe(int requestedCapacity) {
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
    public E poll() {
        long tail = tailSequence.get();
        int index = mask(tail);
        E e = array.getObjectVolatile(index);
        if (e != null) {
            array.setObject(index, null);
            tailSequence.setOrdered(tail + 1);
        }

        return e;
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
