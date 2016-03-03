package org.efix.util.format;

import org.efix.util.TestUtil;
import org.junit.Test;


public class TimeFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP = TestUtil.parseDate("00010101");
    protected static final long MAX_TIMESTAMP = TestUtil.parseDate("99991231");

    protected static final Verifier<Long> VERIFIER = TestUtil::formatTime;
    protected static final Formatter<Long> FORMATTER = TimeFormatter::formatTime;

    @Test
    public void shouldFormatTimes() {
        shouldFormat(0);
        shouldFormat(-1);
        shouldFormat(1);
        shouldFormat(System.currentTimeMillis());
        shouldFormat(-639360000000L);
    }

    @Test
    public void shouldFormatRandomTimes() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(TestUtil.generateLong(MIN_TIMESTAMP, MAX_TIMESTAMP));
    }

    protected static void shouldFormat(long timestamp) {
        shouldFormat(timestamp, VERIFIER, FORMATTER);
    }

}
