package org.efix.schedule;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;

public class WeeklySessionScheduleTest extends AbstractSessionScheduleTest {

    @Test
    public void checkUTCSchedule() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        DayOfWeek startDay = DayOfWeek.MONDAY;
        DayOfWeek endDay = DayOfWeek.FRIDAY;
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new WeeklySessionSchedule(startDay, endDay, startTime, endTime, zoneId);

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
    public void checkNewYorkSchedule() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        DayOfWeek startDay = DayOfWeek.FRIDAY;
        DayOfWeek endDay = DayOfWeek.WEDNESDAY;
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new WeeklySessionSchedule(startDay, endDay, startTime, endTime, zoneId);

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

}
