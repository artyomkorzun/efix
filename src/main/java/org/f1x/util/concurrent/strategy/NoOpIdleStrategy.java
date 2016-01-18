package org.f1x.util.concurrent.strategy;

/**
 * Low-latency idle strategy to be employed in loops that do significant work on each iteration such that any work in the
 * idle strategy would be wasteful.
 */
public final class NoOpIdleStrategy implements IdleStrategy {
    /**
     * <b>Note</b>: this implementation will result in no safepoint poll once inlined.
     *
     * @see IdleStrategy
     */
    public void idle(int workCount) {
    }

}
