package org.f1x.util.concurrent;

import org.f1x.util.LangUtil;
import org.f1x.util.concurrent.strategy.IdleStrategy;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

public class WorkerRunner implements Runnable, AutoCloseable {

    protected static final Thread TOMB = new Thread("TOMB");

    protected final AtomicReference<Thread> thread = new AtomicReference<>();
    protected final Worker worker;
    protected final IdleStrategy strategy;

    public WorkerRunner(Worker worker, IdleStrategy strategy) {
        this.worker = requireNonNull(worker);
        this.strategy = requireNonNull(strategy);
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
        while (worker.active()) {
            int work = worker.doWork();
            strategy.idle(work);
        }
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
        worker.deactivate();
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
