package org.f1x.util.concurrent;

/**
 * All methods are invoked from single thread except for Worker::deactivate.
 */
public interface Worker {

    void onStart();

    void onClose();

    int doWork();


    boolean active();

    void deactivate();

}
