package org.efix.util.concurrent.strategy;

import java.util.concurrent.locks.LockSupport;

/**
 * When idle this strategy is to sleep for a specified period.
 */
public final class SleepingIdleStrategy implements IdleStrategy {

    private final long periodNs;

    /**
     * Constructed a new strategy that will sleep for a given period when idle.
     *
     * @param periodNs period in nanosecond for which the strategy will sleep when work count is 0.
     */
    public SleepingIdleStrategy(long periodNs) {
        this.periodNs = periodNs;
    }

    public void idle(int workCount) {
        if (workCount <= 0)
            LockSupport.parkNanos(periodNs);
    }

}
