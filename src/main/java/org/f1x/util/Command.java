package org.f1x.util;

public interface Command<T> {

    void execute(T target);

}
