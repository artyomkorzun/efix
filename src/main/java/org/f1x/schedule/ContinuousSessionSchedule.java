package org.f1x.schedule;

public final class ContinuousSessionSchedule implements SessionSchedule {

    public static final ContinuousSessionSchedule INSTANCE = new ContinuousSessionSchedule();

    private ContinuousSessionSchedule() {
    }

    @Override
    public long getStartTime(long time) {
        return Long.MIN_VALUE;
    }

    @Override
    public long getEndTime(long time) {
        return Long.MAX_VALUE;
    }

}
