package org.efix.util.concurrent.strategy;

import sun.misc.Contended;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Busy spin strategy targeted at lowest possible latency. This strategy will monopolise a thread to achieve the lowest
 * possible latency. Useful for creating bubbles in the execution pipeline of tight busy spin loops with no other logic than
 * status checks on progress.
 */
public final class BusySpinIdleStrategy implements IdleStrategy {

    @Contended
    protected int dummyCounter;

    /**
     * <b>Note</b>: this implementation will result in no safepoint poll once inlined.
     *
     * @see IdleStrategy#idle(int)
     */
    public void idle(int workCount) {
        if (workCount <= 0) {
            // Trick speculative execution into not progressing
            if (dummyCounter > 0) {
                if (ThreadLocalRandom.current().nextInt() > 0)
                    dummyCounter--;

            } else {
                dummyCounter = 64;
            }
        }

    }

}
