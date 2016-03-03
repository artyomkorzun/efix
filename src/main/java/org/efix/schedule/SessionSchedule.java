package org.efix.schedule;

public interface SessionSchedule {

    long getStartTime(long time);

    long getEndTime(long time);

}
