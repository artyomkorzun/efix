package org.efix.schedule;

import org.junit.Test;

import java.time.LocalTime;
import java.time.ZoneId;

public class DailySessionScheduleTest extends AbstractSessionScheduleTest {

    @Test
    public void checkUTCSchedule() {
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(23, 59, 59);
        ZoneId zoneId = ZoneId.of("UTC");

        SessionSchedule schedule = new DailySessionSchedule(startTime, endTime, zoneId);

        checkSchedule("20160117-00:00:00", "20160117-23:59:59", "20160117-00:00:00", schedule);
        checkSchedule("20160117-00:00:00", "20160117-23:59:59", "20160117-23:59:59", schedule);

        checkSchedule("20160118-00:00:00", "20160118-23:59:59", "20160117-23:59:59.001", schedule);
        checkSchedule("20160118-00:00:00", "20160118-23:59:59", "20160117-23:59:59.999", schedule);
    }

    @Test
    public void checkNewYorkSchedule() {
        // -5 hours from UTC, in 2016 year DST starts 13 March and ends 6 November
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        ZoneId zoneId = ZoneId.of("America/New_York");

        SessionSchedule schedule = new DailySessionSchedule(startTime, endTime, zoneId);

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

}
