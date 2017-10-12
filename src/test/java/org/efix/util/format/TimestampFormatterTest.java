package org.efix.util.format;

import org.efix.util.TestUtil;
import org.efix.util.type.TimestampType;
import org.junit.Test;

import static org.efix.util.TestUtil.generateInt;
import static org.efix.util.TestUtil.generateLong;


public class TimestampFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP_MS = TestUtil.parseDate("20000101");
    protected static final long MAX_TIMESTAMP_MS = TestUtil.parseDate("21000101");

    protected static final long MIN_TIMESTAMP_NS = MIN_TIMESTAMP_MS * 1000 * 1000;
    protected static final long MAX_TIMESTAMP_NS = MAX_TIMESTAMP_MS * 1000 * 1000;

    protected static final Verifier<Long> VERIFIER_MS = TestUtil::formatTimestampMs;
    protected static final Formatter<Long> FORMATTER_MS = TimestampFormatter::formatTimestampMs;

    protected static final Verifier<Long> VERIFIER_NS = TestUtil::formatTimestampNs;
    protected static final Formatter<Long> FORMATTER_NS = TimestampFormatter::formatTimestampNs;


    @Test
    public void shouldFormatTimestamps() {
        shouldFormat(MIN_TIMESTAMP_MS, VERIFIER_MS, FORMATTER_MS);
        shouldFormat(MAX_TIMESTAMP_MS, VERIFIER_MS, FORMATTER_MS);

        shouldFormat(MIN_TIMESTAMP_NS, VERIFIER_NS, FORMATTER_NS);
        shouldFormat(MAX_TIMESTAMP_NS, VERIFIER_NS, FORMATTER_NS);
    }

    @Test
    public void shouldFormatRandomTimestamps() {
        for (int i = 0; i < 10000000; i++) {
            final long timestampMs = generateLong(MIN_TIMESTAMP_MS, MAX_TIMESTAMP_MS);
            final int micros = generateInt(0, 999);
            final int nanos = generateInt(0, 999);

            shouldFormat(timestampMs, VERIFIER_MS, FORMATTER_MS);
            shouldFormat((timestampMs * 1000 + micros) * 1000 + nanos, VERIFIER_NS, FORMATTER_NS);
        }
    }

    @Test
    public void shouldFailFormatDatesOutOfRange() {
        shouldFailFormat(MIN_TIMESTAMP_MS - 1, TimestampType.MILLISECOND_TIMESTAMP_LENGTH, FORMATTER_MS);
        shouldFailFormat(MAX_TIMESTAMP_MS + 1, TimestampType.MILLISECOND_TIMESTAMP_LENGTH, FORMATTER_MS);

        shouldFailFormat(MIN_TIMESTAMP_NS - 1, TimestampType.NANOSECOND_TIMESTAMP_LENGTH, FORMATTER_NS);
        shouldFailFormat(MAX_TIMESTAMP_NS + 1, TimestampType.NANOSECOND_TIMESTAMP_LENGTH, FORMATTER_NS);
    }


}
