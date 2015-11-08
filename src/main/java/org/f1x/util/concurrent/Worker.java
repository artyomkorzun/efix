package org.f1x.util.concurrent;

public interface Worker {

    void onStart();

    void onClose();

    void doWork();

}
