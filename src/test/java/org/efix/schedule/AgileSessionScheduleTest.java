package org.efix.schedule;

import org.efix.schedule.AgileSessionSchedule.Interval;
import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;


public class AgileSessionScheduleTest extends AbstractSessionScheduleTest {

    @Test(expected = IllegalArgumentException.class)
    public void checkIntersection() {
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(LocalTime.of(19, 0), LocalTime.of(17, 0), DayOfWeek.SATURDAY, DayOfWeek.TUESDAY),
                new Interval(LocalTime.of(17, 0), LocalTime.of(17, 0), DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
        );
    }

    @Test
    public void checkInfiniteSchedule() {
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId);
        Assert.assertEquals(Long.MIN_VALUE, schedule.getStartTime(0));
        Assert.assertEquals(Long.MAX_VALUE, schedule.getEndTime(0));
    }

    @Test
    public void checkUTCScheduleAgile() {
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(LocalTime.of(17, 0), LocalTime.of(19, 0), DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                new Interval(LocalTime.of(15, 0), LocalTime.of(14, 59), DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                new Interval(LocalTime.of(23, 0), LocalTime.of(14, 0), DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
        );

        checkSchedule("20170813-23:00:00", "20170814-14:00:00", "20170812-19:00:01", schedule);
        checkSchedule("20170813-23:00:00", "20170814-14:00:00", "20170813-22:59:59", schedule);
        checkSchedule("20170813-23:00:00", "20170814-14:00:00", "20170813-23:00:00", schedule);
        checkSchedule("20170813-23:00:00", "20170814-14:00:00", "20170814-14:00:00", schedule);

        checkSchedule("20170814-15:00:00", "20170816-14:59:00", "20170814-14:59:59", schedule);
        checkSchedule("20170814-15:00:00", "20170816-14:59:00", "20170814-15:00:00", schedule);
        checkSchedule("20170814-15:00:00", "20170816-14:59:00", "20170816-14:59:00", schedule);

        checkSchedule("20170818-17:00:00", "20170819-19:00:00", "20170818-16:49:49", schedule);
        checkSchedule("20170818-17:00:00", "20170819-19:00:00", "20170818-17:00:00", schedule);
        checkSchedule("20170818-17:00:00", "20170819-19:00:00", "20170819-19:00:00", schedule);
    }

    @Test
    public void checkUTCScheduleDaily() {
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(23, 59, 59);
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(startTime, endTime, DayOfWeek.FRIDAY, DayOfWeek.FRIDAY),
                new Interval(startTime, endTime, DayOfWeek.TUESDAY, DayOfWeek.TUESDAY),
                new Interval(startTime, endTime, DayOfWeek.SATURDAY, DayOfWeek.SATURDAY),
                new Interval(startTime, endTime, DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY),
                new Interval(startTime, endTime, DayOfWeek.MONDAY, DayOfWeek.MONDAY),
                new Interval(startTime, endTime, DayOfWeek.THURSDAY, DayOfWeek.THURSDAY),
                new Interval(startTime, endTime, DayOfWeek.SUNDAY, DayOfWeek.SUNDAY)
        );

        checkSchedule("20160117-00:00:00", "20160117-23:59:59", "20160117-00:00:00", schedule);
        checkSchedule("20160117-00:00:00", "20160117-23:59:59", "20160117-23:59:59", schedule);

        checkSchedule("20160118-00:00:00", "20160118-23:59:59", "20160117-23:59:59.001", schedule);
        checkSchedule("20160118-00:00:00", "20160118-23:59:59", "20160117-23:59:59.999", schedule);
    }

    @Test
    public void checkNewYorkScheduleDaily() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(startTime, endTime, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                new Interval(startTime, endTime, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
                new Interval(startTime, endTime, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                new Interval(startTime, endTime, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY),
                new Interval(startTime, endTime, DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                new Interval(startTime, endTime, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                new Interval(startTime, endTime, DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
        );
        checkSchedule("20160117-22:00:00", "20160118-21:00:00", "20160117-22:00:00", schedule);
        checkSchedule("20160117-22:00:00", "20160118-21:00:00", "20160118-21:00:00", schedule);

        checkSchedule("20160117-22:00:00", "20160118-21:00:00", "20160117-21:00:00.001", schedule);
        checkSchedule("20160117-22:00:00", "20160118-21:00:00", "20160117-21:59:59.999", schedule);

        // DST start
        checkSchedule("20160312-22:00:00", "20160313-20:00:00", "20160312-21:30:00", schedule);
        checkSchedule("20160313-21:00:00", "20160314-20:00:00", "20160313-21:30:00", schedule);

        // DST end
        checkSchedule("20161105-21:00:00", "20161106-21:00:00", "20161105-20:30:00", schedule);
        checkSchedule("20161105-21:00:00", "20161106-21:00:00", "20161106-20:59:00", schedule);
    }

    @Test
    public void checkUTCScheduleWeekly() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        DayOfWeek startDay = DayOfWeek.MONDAY;
        DayOfWeek endDay = DayOfWeek.FRIDAY;
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId, new Interval(startTime, endTime, startDay, endDay));

        // Sunday 1/17/2016 00:00
        checkSchedule("20160118-08:00:00", "20160122-17:00:00", "20160117-00:00:00", schedule);
        // Wednesday 1/20/2016 22:00
        checkSchedule("20160118-08:00:00", "20160122-17:00:00", "20160120-22:00:00", schedule);
        // Friday 1/22/2016 17:00
        checkSchedule("20160118-08:00:00", "20160122-17:00:00", "20160122-17:00:00", schedule);

        // Friday 1/22/2016 17:00:00.001
        checkSchedule("20160125-08:00:00", "20160129-17:00:00", "20160122-17:00:00.001", schedule);
        // Monday 1/25/2016 07:59:59.999
        checkSchedule("20160125-08:00:00", "20160129-17:00:00", "20160125-07:59:59.999", schedule);
    }

    @Test
    public void checkNewYorkScheduleWeekly() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        DayOfWeek startDay = DayOfWeek.FRIDAY;
        DayOfWeek endDay = DayOfWeek.WEDNESDAY;
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId, new Interval(startTime, endTime, startDay, endDay));

        // Friday 1/15/2016 17:00
        checkSchedule("20160115-22:00:00", "20160120-21:00:00", "20160115-22:00:00", schedule);
        // Sunday 1/17/2016 16:30
        checkSchedule("20160115-22:00:00", "20160120-21:00:00", "20160117-21:30:00", schedule);
        // Wednesday 1/20/2016 16:00
        checkSchedule("20160115-22:00:00", "20160120-21:00:00", "20160120-21:00:00", schedule);

        // Wednesday 1/20/2016 16:00:00.001
        checkSchedule("20160122-22:00:00", "20160127-21:00:00", "20160120-21:00:00.001", schedule);
        // Friday 1/22/2016 16:59:59.999
        checkSchedule("20160122-22:00:00", "20160127-21:00:00", "20160122-21:59:59.999", schedule);

        // DST start
        // Friday 3/11/2016 18:00:00
        checkSchedule("20160311-22:00:00", "20160316-20:00:00", "20160311-23:00:00", schedule);
        // Wednesday 3/16/2016 16:00:00.001
        checkSchedule("20160318-21:00:00", "20160323-20:00:00", "20160316-20:00:00.001", schedule);

        // DST end
        // Friday 11/04/2016 16:30
        checkSchedule("20161104-21:00:00", "20161109-21:00:00", "20161104-20:30:00", schedule);
        // Wednesday 11/09/2016 16:59
        checkSchedule("20161104-21:00:00", "20161109-21:00:00", "20161109-20:59:00", schedule);
    }

    @Test
    public void checkUTCScheduleWeeklyDaily() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(startTime, endTime, DayOfWeek.TUESDAY, DayOfWeek.TUESDAY),
                new Interval(startTime, endTime, DayOfWeek.MONDAY, DayOfWeek.MONDAY),
                new Interval(startTime, endTime, DayOfWeek.THURSDAY, DayOfWeek.THURSDAY),
                new Interval(startTime, endTime, DayOfWeek.WEDNESDAY, DayOfWeek.WEDNESDAY),
                new Interval(startTime, endTime, DayOfWeek.FRIDAY, DayOfWeek.FRIDAY)
        );

        // Sunday 1/17/2016 00:00
        checkSchedule("20160118-08:00:00", "20160118-17:00:00", "20160117-00:00:00", schedule);
        // Wednesday 1/20/2016 22:00
        checkSchedule("20160121-08:00:00", "20160121-17:00:00", "20160120-22:00:00", schedule);
        // Friday 1/22/2016 17:00
        checkSchedule("20160122-08:00:00", "20160122-17:00:00", "20160122-17:00:00", schedule);

        // Friday 1/22/2016 17:00:00.001
        checkSchedule("20160125-08:00:00", "20160125-17:00:00", "20160122-17:00:00.001", schedule);
        // Monday 1/25/2016 07:59:59.999
        checkSchedule("20160125-08:00:00", "20160125-17:00:00", "20160125-07:59:59.999", schedule);
    }

    @Test
    public void checkNewYorkScheduleWeeklyDaily() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new AgileSessionSchedule(zoneId,
                new Interval(startTime, endTime, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                new Interval(startTime, endTime, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
                new Interval(startTime, endTime, DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
                new Interval(startTime, endTime, DayOfWeek.SUNDAY, DayOfWeek.MONDAY),
                new Interval(startTime, endTime, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)
        );

        // Friday 1/15/2016 17:00
        checkSchedule("20160115-22:00:00", "20160116-21:00:00", "20160115-22:00:00", schedule);
        // Sunday 1/17/2016 16:30
        checkSchedule("20160117-22:00:00", "20160118-21:00:00", "20160117-21:30:00", schedule);
        // Wednesday 1/20/2016 16:00
        checkSchedule("20160119-22:00:00", "20160120-21:00:00", "20160120-21:00:00", schedule);

        // Wednesday 1/20/2016 16:00:00.001
        checkSchedule("20160122-22:00:00", "20160123-21:00:00", "20160120-21:00:00.001", schedule);
        // Friday 1/22/2016 16:59:59.999
        checkSchedule("20160122-22:00:00", "20160123-21:00:00", "20160122-21:59:59.999", schedule);

        // DST start
        // Saturday 3/12/2016 18:00:00
        checkSchedule("20160312-22:00:00", "20160313-20:00:00", "20160312-23:00:00", schedule);
        // Sunday 3/13/2016 16:00:00.001
        checkSchedule("20160313-21:00:00", "20160314-20:00:00", "20160313-20:00:00.001", schedule);

        // DST end
        // Saturday 11/05/2016 16:30
        checkSchedule("20161105-21:00:00", "20161106-21:00:00", "20161105-20:30:00", schedule);
        // Sunday 11/06/2016 16:59
        checkSchedule("20161105-21:00:00", "20161106-21:00:00", "20161106-20:59:00", schedule);
    }

}
