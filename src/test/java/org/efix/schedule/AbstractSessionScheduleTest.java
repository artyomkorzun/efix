package org.efix.schedule;

import org.efix.util.TestUtil;

import static org.efix.util.TestUtil.parseTimestampMs;
import static org.junit.Assert.assertEquals;

public class AbstractSessionScheduleTest {

    protected static void checkSchedule(String expectedStart, String expectedEnd, String now, SessionSchedule schedule) {
        long timestamp = parseTimestampMs(now);
        long start = schedule.getStartTime(timestamp);
        long end = schedule.getEndTime(timestamp);

        assertTimestamp("session start expected %s, actual %s", expectedStart, start);
        assertTimestamp("session end expected %s, actual %s", expectedEnd, end);
    }

    protected static void assertTimestamp(String message, String expected, long actual) {
        message = String.format(message, expected, TestUtil.formatTimestampMs(actual));
        assertEquals(message, parseTimestampMs(expected), actual);
    }
}
