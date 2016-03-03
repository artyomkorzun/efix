package org.efix.schedule;

import java.time.*;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public abstract class AbstractSessionSchedule implements SessionSchedule {

    protected final LocalTime startTime;
    protected final LocalTime endTime;

    protected final ZoneId zoneId;

    protected long startTimestamp = Long.MIN_VALUE;
    protected long endTimestamp = Long.MIN_VALUE;

    protected long lastTime = Long.MIN_VALUE;

    protected AbstractSessionSchedule(LocalTime startTime, LocalTime endTime, ZoneId zoneId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.zoneId = zoneId;
    }

    @Override
    public long getStartTime(long time) {
        updateIfNeeded(time);
        return startTimestamp;
    }

    @Override
    public long getEndTime(long time) {
        updateIfNeeded(time);
        return endTimestamp;
    }

    protected void updateIfNeeded(long time) {
        if (shouldUpdate(time)) {
            update(time);
            lastTime = time;
        }
    }

    protected boolean shouldUpdate(long time) {
        return time < Math.min(lastTime, startTimestamp) || time > endTimestamp;
    }

    protected abstract void update(long time);

    protected static long millis(ZonedDateTime dateTime) {
        return dateTime.toInstant().toEpochMilli();
    }

    protected static ZonedDateTime dateTimeOf(long millis, ZoneId zoneId) {
        return Instant.ofEpochMilli(millis).atZone(zoneId);
    }

    protected static ZonedDateTime dateWithTime(ZonedDateTime date, LocalTime time) {
        return date.withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(time.getSecond())
                .withNano(time.getNano());
    }

    protected static ZonedDateTime dateTimeAfter(LocalTime newTime, ZonedDateTime now) {
        ZonedDateTime date = dateWithTime(now, newTime);
        return date.isBefore(now) ? date.plusDays(1) : date;
    }

    protected static ZonedDateTime dateTimeAfter(LocalTime newTime, DayOfWeek newDay, ZonedDateTime now) {
        ZonedDateTime date = dateWithTime(now, newTime).with(nextOrSame(newDay));
        return date.isBefore(now) ? date.plusWeeks(1) : date;
    }

    protected static ZonedDateTime dateTimeBefore(LocalTime newTime, ZonedDateTime now) {
        ZonedDateTime date = dateWithTime(now, newTime);
        return date.isAfter(now) ? date.minusDays(1) : date;
    }

    protected static ZonedDateTime dateTimeBefore(LocalTime newTime, DayOfWeek newDay, ZonedDateTime now) {
        ZonedDateTime date = dateWithTime(now, newTime).with(previousOrSame(newDay));
        return date.isAfter(now) ? date.minusWeeks(1) : date;
    }

}
