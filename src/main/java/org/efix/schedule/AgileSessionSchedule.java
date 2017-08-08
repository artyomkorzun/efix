package org.efix.schedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;


public class AgileSessionSchedule extends AbstractSessionSchedule {

    protected final Interval[] intervals;

    public AgileSessionSchedule(final ZoneId zoneId, final Interval... intervals) {
        super(null, null, zoneId);

        if (intervals == null || intervals.length == 0) {
            this.intervals = null;
            this.startTimestamp = Long.MIN_VALUE;
            this.endTimestamp = Long.MAX_VALUE;
        } else {
            this.intervals = buildIntervals(intervals);
        }
    }

    @Override
    protected void update(final long time) {
        ZonedDateTime now = dateTimeOf(time, zoneId);
        Interval interval = findInterval(now);

        ZonedDateTime end = dateTimeAfter(interval.endTime, interval.endDay, now);
        ZonedDateTime start = dateTimeBefore(interval.startTime, interval.startDay, end);

        startTimestamp = millis(start);
        endTimestamp = millis(end);
    }

    protected Interval findInterval(ZonedDateTime now) {
        Interval interval = intervals[intervals.length - 1];
        ZonedDateTime last = dateTimeAfter(interval.endTime, interval.endDay, now);

        for (Interval candidate : intervals) {
            ZonedDateTime end = dateTimeBefore(candidate.endTime, candidate.endDay, last);
            if (!end.isBefore(now)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Unreachable code");
    }

    protected static Interval[] buildIntervals(Interval[] intervals) {
        Comparator<Interval> dayComparator = Comparator.comparingInt(o -> o.getEndDay().getValue());
        Comparator<Interval> timeComparator = Comparator.comparing(Interval::getEndTime);

        intervals = Arrays.copyOf(intervals, intervals.length);
        Arrays.sort(intervals, dayComparator.thenComparing(timeComparator));

        for (int i = 1; i < intervals.length; i++) {
            Interval previous = intervals[i - 1];
            Interval current = intervals[i];

            verifyNoIntersection(previous, current);
        }

        if (intervals.length > 1) {
            Interval first = intervals[0];

            if (first.crossWeek()) {
                Interval last = intervals[intervals.length - 1];
                verifyNoIntersection(last, first);
            }
        }

        return intervals;
    }

    protected static void verifyNoIntersection(Interval previous, Interval current) {
        LocalTime endTime = previous.getEndTime();
        LocalTime startTime = current.getStartTime();

        DayOfWeek endDay = previous.getEndDay();
        DayOfWeek startDay = current.getStartDay();

        if ((startDay.compareTo(endDay) < 0) || ((startDay == endDay) && (startTime.compareTo(endTime) <= 0))) {
            throw new IllegalArgumentException("Intervals intersect: " + previous + " and " + current);
        }
    }

    public final static class Interval {

        protected final LocalTime startTime;
        protected final LocalTime endTime;
        protected final DayOfWeek startDay;
        protected final DayOfWeek endDay;

        public Interval(final LocalTime startTime, final LocalTime endTime, final DayOfWeek startDay, final DayOfWeek endDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.startDay = startDay;
            this.endDay = endDay;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public DayOfWeek getStartDay() {
            return startDay;
        }

        public DayOfWeek getEndDay() {
            return endDay;
        }

        public boolean crossWeek() {
            return (startDay.compareTo(endDay) > 0) || ((startDay == endDay) && (startTime.compareTo(endTime) > 0));
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", startDay=" + startDay +
                    ", endDay=" + endDay +
                    '}';
        }

    }

}
