package org.f1x.util.format;

import org.f1x.util.TestUtil;
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
        shouldFormat(System.currentTimeMillis());
    }

    @Test
    public void shouldFormatRandomTimestamps() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(generateLong(MIN_TIMESTAMP, MAX_TIMESTAMP));
    }

    protected static void shouldFormat(long timestamp) {
        shouldFormat(timestamp, VERIFIER, FORMATTER);
    }

}
