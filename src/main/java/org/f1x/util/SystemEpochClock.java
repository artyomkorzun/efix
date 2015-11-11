package org.f1x.util;


public final class SystemEpochClock implements EpochClock {

    public static final SystemEpochClock INSTANCE = new SystemEpochClock();

    @Override
    public long time() {
        return System.currentTimeMillis();
    }

}
