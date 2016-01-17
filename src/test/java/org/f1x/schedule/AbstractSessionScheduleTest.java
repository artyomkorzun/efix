package org.f1x.schedule;

import static org.f1x.util.TestUtil.formatTimestamp;
import static org.f1x.util.TestUtil.parseTimestamp;
import static org.junit.Assert.assertEquals;

public class AbstractSessionScheduleTest {

    protected static void checkSchedule(String expectedStart, String expectedEnd, String now, SessionSchedule schedule) {
        long timestamp = parseTimestamp(now);
        long start = schedule.getStartTime(timestamp);
        long end = schedule.getEndTime(timestamp);

        assertTimestamp("session start expected %s, actual %s", expectedStart, start);
        assertTimestamp("session end expected %s, actual %s", expectedEnd, end);
    }

    protected static void assertTimestamp(String message, String expected, long actual) {
        message = String.format(message, expected, formatTimestamp(actual));
        assertEquals(message, parseTimestamp(expected), actual);
    }
}
