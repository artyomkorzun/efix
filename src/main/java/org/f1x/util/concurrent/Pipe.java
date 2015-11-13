package org.f1x.util.concurrent;

import java.util.function.Consumer;


public interface Pipe<E> {

    boolean offer(E e);

    E poll();

    int drain(Consumer<E> handler);

    int capacity();

    int size();

}
