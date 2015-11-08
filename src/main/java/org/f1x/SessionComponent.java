package org.f1x;

public interface SessionComponent extends AutoCloseable {

    void open();

    void flush();

    void close();

}
