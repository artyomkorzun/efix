package org.f1x.util.format;

import org.f1x.util.TestUtil;
import org.f1x.util.type.TimestampType;
import org.junit.Test;

import static org.f1x.util.TestUtil.generateLong;


public class TimestampFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP = TestUtil.parseDate("19040101");
    protected static final long MAX_TIMESTAMP = TestUtil.parseDate("21000101");

    protected static final Verifier<Long> VERIFIER = TestUtil::formatTimestamp;
    protected static final Formatter<Long> FORMATTER = TimestampFormatter::formatTimestamp;

    @Test
    public void shouldFormatTimestamps() {
        shouldFormat(0);
        shouldFormat(-1);
        shouldFormat(1);
        shouldFormat(-639360000000L);
        shouldFormat(System.currentTimeMillis());
    }

    @Test
    public void shouldFormatRandomTimestamps() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(generateLong(MIN_TIMESTAMP, MAX_TIMESTAMP));
    }

    @Test
    public void shouldFailFormatDatesOutOfRange() {
        shouldFailFormat(MIN_TIMESTAMP - 1);
        shouldFailFormat(MAX_TIMESTAMP + 1);
    }

    protected static void shouldFormat(long timestamp) {
        shouldFormat(timestamp, VERIFIER, FORMATTER);
    }

    protected static void shouldFailFormat(long timestamp) {
        shouldFailFormat(timestamp, TimestampType.MILLISECOND_TIMESTAMP_LENGTH, FORMATTER);
    }

}
