package org.f1x.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WeeklySessionSchedule extends AbstractSessionSchedule {

    protected final DayOfWeek startDay;
    protected final DayOfWeek endDay;

    public WeeklySessionSchedule(DayOfWeek startDay, DayOfWeek endDay, LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        super(startTime, endTime, zoneId);
        this.startDay = startDay;
        this.endDay = endDay;
    }

    @Override
    protected void update(long time) {
        ZonedDateTime now = dateTimeOf(time, zoneId);
        ZonedDateTime end = dateTimeAfter(endTime, endDay, now);
        ZonedDateTime start = dateTimeBefore(startTime, startDay, end);

        startTimestamp = millis(start);
        endTimestamp = millis(end);
    }

}
