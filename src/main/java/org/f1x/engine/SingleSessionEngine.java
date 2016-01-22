package org.f1x.engine;

import org.f1x.util.Command;
import org.f1x.util.Disposable;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.concurrent.WorkerRunner;
import org.f1x.util.concurrent.buffer.RingBuffer;
import org.f1x.util.concurrent.queue.Queue;

public class SingleSessionEngine implements Disposable {

    protected final Queue<Command<SessionProcessor>> commandQueue;
    protected final RingBuffer messageQueue;

    protected final SessionProcessor processor;
    protected final WorkerRunner runner;
    protected final Thread thread;

    public SingleSessionEngine(SessionContext context, SessionProcessor processor) {
        this.commandQueue = context.commandQueue();
        this.messageQueue = context.messageQueue();
        this.processor = processor;
        this.runner = new WorkerRunner(processor, context.idleStrategy());
        this.thread = new Thread(runner, threadName(context));
    }

    @Override
    public void open() {
        thread.start();
    }

    @Override
    public void close() {
        offer(CloseCommand.INSTANCE);
        runner.close();
    }

    public void sendMessage(Buffer buffer, int offset, int length) {
        RingBuffer queue = this.messageQueue;
        while (!queue.write(EventType.OUTBOUND_MESSAGE, buffer, offset, length))
            Thread.yield();
    }

    protected void offer(Command<SessionProcessor> command) {
        Queue<Command<SessionProcessor>> queue = this.commandQueue;
        while (!queue.offer(command))
            Thread.yield();
    }

    protected static String threadName(SessionContext context) {
        return String.format("Session fixVersion %s, %s", context.fixVersion().beginString(), context.sessionID());
    }

}
