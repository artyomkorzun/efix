package org.efix.schedule;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DailySessionSchedule extends AbstractSessionSchedule {

    public DailySessionSchedule(LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        super(startTime, endTime, zoneId);
    }

    @Override
    protected void update(long time) {
        ZonedDateTime now = dateTimeOf(time, zoneId);
        ZonedDateTime end = dateTimeAfter(endTime, now);
        ZonedDateTime start = dateTimeBefore(startTime, end);

        startTimestamp = millis(start);
        endTimestamp = millis(end);
    }

}
