package org.efix.util.format;

import org.efix.util.TestUtil;
import org.efix.util.type.TimestampType;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.efix.util.TestUtil.generateInt;
import static org.efix.util.TestUtil.generateLong;


public class TimestampFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP_MS = TestUtil.parseDate("20000101");
    protected static final long MAX_TIMESTAMP_MS = TestUtil.parseDate("21000101");

    protected static final Verifier<Long> VERIFIER = TestUtil::formatTimestampMs;
    protected static final Formatter<Long> FORMATTER = TimestampFormatter::formatTimestamp;

    protected static final Verifier<Long> VERIFIER_S = TestUtil::formatTimestampS;
    protected static final Formatter<Long> FORMATTER_S = (timestampMs, buffer, offset) -> TimestampFormatter.formatTimestamp(timestampMs, TimeUnit.SECONDS, buffer, offset);

    protected static final Verifier<Long> VERIFIER_MS = TestUtil::formatTimestampMs;
    protected static final Formatter<Long> FORMATTER_MS = (timestampMs, buffer, offset) -> TimestampFormatter.formatTimestamp(timestampMs, TimeUnit.MILLISECONDS, buffer, offset);

    protected static final Verifier<Long> VERIFIER_US = TestUtil::formatTimestampUs;
    protected static final Formatter<Long> FORMATTER_US = (timestampMs, buffer, offset) -> TimestampFormatter.formatTimestamp(timestampMs, TimeUnit.MICROSECONDS, buffer, offset);

    protected static final Verifier<Long> VERIFIER_NS = TestUtil::formatTimestampNs;
    protected static final Formatter<Long> FORMATTER_NS = (timestampMs, buffer, offset) -> TimestampFormatter.formatTimestamp(timestampMs, TimeUnit.NANOSECONDS, buffer, offset);


    @Test
    public void shouldFormatTimestamps() {
        shouldFormat(MIN_TIMESTAMP_MS);
        shouldFormat(MAX_TIMESTAMP_MS);
    }

    @Test
    public void shouldFormatRandomTimestamps() {
        for (int i = 0; i < 10000000; i++) {
            final long timestampMs = generateLong(MIN_TIMESTAMP_MS, MAX_TIMESTAMP_MS);
            final int micros = generateInt(0, 999);
            final int nanos = generateInt(0, 999);

            shouldFormat(timestampMs);
            shouldFormat(timestampMs / 1000, VERIFIER_S, FORMATTER_S);
            shouldFormat(timestampMs, VERIFIER_MS, FORMATTER_MS);
            shouldFormat(timestampMs * 1000 + micros, VERIFIER_US, FORMATTER_US);
            shouldFormat((timestampMs * 1000 + micros) * 1000 + nanos, VERIFIER_NS, FORMATTER_NS);
        }
    }

    @Test
    public void shouldFailFormatDatesOutOfRange() {
        shouldFailFormat(MIN_TIMESTAMP_MS - 1);
        shouldFailFormat(MAX_TIMESTAMP_MS + 1);
    }

    protected static void shouldFormat(long timestamp) {
        shouldFormat(timestamp, VERIFIER, FORMATTER);
    }

    protected static void shouldFailFormat(long timestamp) {
        shouldFailFormat(timestamp, TimestampType.MILLISECOND_TIMESTAMP_LENGTH, FORMATTER);
    }

}
