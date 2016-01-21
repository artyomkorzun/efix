package org.f1x.engine;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.concurrent.WorkerRunner;
import org.f1x.util.concurrent.buffer.RingBuffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncSessionEngine implements SessionEngine {

    protected final RingBuffer messageQueue;
    protected final SessionProcessor processor;
    protected final WorkerRunner runner;
    protected final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AsyncSessionEngine(SessionContext context, SessionProcessor processor) {
        this.messageQueue = context.messageQueue();
        this.processor = processor;
        this.runner = new WorkerRunner(processor);
    }

    @Override
    public void start() {
        executor.execute(runner);
    }

    @Override
    public void close() {
        // TODO: processor.close();
        executor.shutdown();
        runner.close();
    }

    @Override
    public void sendMessage(Buffer buffer, int offset, int length) {
        RingBuffer queue = this.messageQueue;
        while (!queue.write(EventTypes.OUTBOUND_MESSAGE, buffer, offset, length))
            Thread.yield();
    }

}
