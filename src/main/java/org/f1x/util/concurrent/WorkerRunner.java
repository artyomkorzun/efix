package org.f1x.util.concurrent;

import org.f1x.util.LangUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class WorkerRunner implements Runnable, AutoCloseable {

    protected static final Thread TOMB = new Thread("TOMB");

    protected final AtomicReference<Thread> thread = new AtomicReference<>();
    protected final Worker worker;

    protected volatile boolean active = true;

    public WorkerRunner(Worker worker) {
        this.worker = Objects.requireNonNull(worker);
    }

    @Override
    public void run() {
        if (!thread.compareAndSet(null, Thread.currentThread()))
            throw new IllegalStateException();

        try {
            onStart();
            work();
        } finally {
            onClose();
        }
    }

    protected void onStart() {
        worker.onStart();
    }

    protected void work() {
        while (active)
            worker.doWork();
    }

    protected void onClose() {
        try {
            worker.onClose();
        } finally {
            thread.set(TOMB);
        }
    }

    @Override
    public void close() {
        active = false;
        Thread thread = this.thread.get();
        if (thread != null && thread != TOMB) {
            try {
                thread.join(1000);
                while (thread.isAlive()) {
                    System.err.println("Timeout await expired for worker. Retrying...");
                    thread.join(1000);
                }
            } catch (InterruptedException e) {
                LangUtil.rethrowUnchecked(e);
            }
        }
    }

}
