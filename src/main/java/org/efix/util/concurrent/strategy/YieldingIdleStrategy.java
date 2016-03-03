package org.efix.util.concurrent.strategy;

/**
 * {@link IdleStrategy} that will call {@link Thread#yield()} when the work count is zero.
 */
public final class YieldingIdleStrategy implements IdleStrategy {

    public void idle(int workCount) {
        if (workCount <= 0)
            Thread.yield();
    }

}
