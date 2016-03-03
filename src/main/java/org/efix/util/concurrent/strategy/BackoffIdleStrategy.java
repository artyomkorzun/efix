package org.efix.util.concurrent.strategy;

import sun.misc.Contended;

import java.util.concurrent.locks.LockSupport;

public final class BackoffIdleStrategy implements IdleStrategy {

    @Contended("fields")
    private final long maxSpins, maxYields, minParkPeriodNs, maxParkPeriodNs;

    @Contended("fields")
    private long spins, yields, parkPeriodNs;

    @Contended("fields")
    private State state = State.NOT_IDLE;

    /**
     * Create a set of state tracking idle behavior
     *
     * @param maxSpins        to perform before moving to {@link Thread#yield()}
     * @param maxYields       to perform before moving to {@link LockSupport#parkNanos(long)}
     * @param minParkPeriodNs to use when initiating parking
     * @param maxParkPeriodNs to use when parking
     */
    public BackoffIdleStrategy(long maxSpins, long maxYields, long minParkPeriodNs, long maxParkPeriodNs) {
        this.maxSpins = maxSpins;
        this.maxYields = maxYields;
        this.minParkPeriodNs = minParkPeriodNs;
        this.maxParkPeriodNs = maxParkPeriodNs;
    }

    /**
     * {@inheritDoc}
     */
    public void idle(int workCount) {
        if (workCount > 0)
            reset();
        else
            idle();
    }

    private void idle() {
        switch (state) {
            case NOT_IDLE:
                state = State.SPINNING;
                spins = 0;
                spins++;
                break;

            case SPINNING:
                if (++spins > maxSpins) {
                    state = State.YIELDING;
                    yields = 0;
                }
                break;

            case YIELDING:
                if (++yields > maxYields) {
                    state = State.PARKING;
                    parkPeriodNs = minParkPeriodNs;
                } else {
                    Thread.yield();
                }
                break;

            case PARKING:
                LockSupport.parkNanos(parkPeriodNs);
                parkPeriodNs = Math.min(parkPeriodNs << 1, maxParkPeriodNs);
                break;
        }
    }

    private void reset() {
        state = State.NOT_IDLE;
    }

    private enum State {

        NOT_IDLE, SPINNING, YIELDING, PARKING

    }

}



