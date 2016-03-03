package org.efix.util;

public interface Disposable extends AutoCloseable {

    void open();

    void close();

}
