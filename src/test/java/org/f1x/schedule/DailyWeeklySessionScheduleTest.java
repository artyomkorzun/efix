package org.f1x.schedule;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;

public class DailyWeeklySessionScheduleTest extends AbstractSessionScheduleTest {

    @Test
    public void checkUTCSchedule() {
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        DayOfWeek startDay = DayOfWeek.MONDAY;
        DayOfWeek endDay = DayOfWeek.FRIDAY;
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new DailyWeeklySessionSchedule(startDay, endDay, startTime, endTime, zoneId);

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
    public void checkNewYorkSchedule() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        DayOfWeek startDay = DayOfWeek.FRIDAY;
        DayOfWeek endDay = DayOfWeek.WEDNESDAY;
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new DailyWeeklySessionSchedule(startDay, endDay, startTime, endTime, zoneId);

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
