package org.efix.util;

public interface Command<T> {

    void execute(T target);

}
