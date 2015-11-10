package org.f1x.connector.channel;

import java.net.SocketOption;
import java.util.ArrayList;

public class SocketOptions {

    protected final ArrayList<Entry<?>> options = new ArrayList<>();

    public <T> void add(SocketOption<T> option, T value) {
        options.add(new Entry<>(option, value));
    }

    public Entry<?> get(int index) {
        return options.get(index);
    }

    public void remove(int index) {
        options.remove(index);
    }

    public void clear() {
        options.clear();
    }

    public int size() {
        return options.size();
    }

    public static class Entry<T> {

        protected final SocketOption<T> option;
        protected final T value;

        public Entry(SocketOption<T> option, T value) {
            this.option = option;
            this.value = value;
        }

        public SocketOption<T> option() {
            return option;
        }

        public T value() {
            return value;
        }

    }

}
