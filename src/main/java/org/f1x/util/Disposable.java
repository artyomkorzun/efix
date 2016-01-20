package org.f1x.util;

public interface Disposable extends AutoCloseable {

    void open();

    void close();

}
