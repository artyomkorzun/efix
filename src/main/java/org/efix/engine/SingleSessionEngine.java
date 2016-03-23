package org.efix.engine;

import org.efix.util.Command;
import org.efix.util.Disposable;
import org.efix.util.buffer.Buffer;
import org.efix.util.concurrent.WorkerRunner;
import org.efix.util.concurrent.buffer.RingBuffer;
import org.efix.util.concurrent.queue.Queue;


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
        return String.format(
                "Session %s %s -> %s",
                context.fixVersion().beginString(),
                context.sessionId().senderCompId(),
                context.sessionId().targetCompId()
        );
    }

}
