package org.f1x.util.format;

import org.f1x.util.TestUtil;
import org.f1x.util.type.DateType;
import org.junit.Test;

import static org.f1x.util.TestUtil.generateLong;


public class DateFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP = TestUtil.parseDate("19040101");
    protected static final long MAX_TIMESTAMP = TestUtil.parseDate("21000101");

    protected static final Verifier<Long> VERIFIER = TestUtil::formatDate;
    protected static final Formatter<Long> FORMATTER = DateFormatter::formatDate;

    @Test
    public void shouldFormatDates() {
        shouldFormat(MIN_TIMESTAMP);
        shouldFormat(MAX_TIMESTAMP);
        shouldFormat(3124193485230L);   // 20681231
    }

    @Test
    public void shouldFormatRandomDates() {
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
        shouldFailFormat(timestamp, DateType.LENGTH, FORMATTER);
    }

}
