package org.f1x.util;

public class HaltedEpochClock implements EpochClock {

    protected long time;

    @Override
    public long time() {
        return time;
    }

    public HaltedEpochClock time(long time) {
        this.time = time;
        return this;
    }

}
