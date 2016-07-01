package org.efix.util;


public class HaltedEpochClock implements EpochClock {

    protected long time;

    public HaltedEpochClock(long time) {
        this.time = time;
    }

    public HaltedEpochClock() {
    }

    @Override
    public long time() {
        return time;
    }

    public HaltedEpochClock time(long time) {
        this.time = time;
        return this;
    }

}
