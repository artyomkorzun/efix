package org.f1x.util.format.newone;

import org.f1x.util.TestUtil;
import org.junit.Test;

import static org.f1x.util.TestUtil.generateLong;

public class DateFormatterTest extends AbstractFormatterTest {

    protected static final long MIN_TIMESTAMP = TestUtil.parseDate("19040101");
    protected static final long MAX_TIMESTAMP = TestUtil.parseDate("21000101");

    protected static final Verifier<Long> VERIFIER = TestUtil::formatDate;
    protected static final Formatter<Long> FORMATTER = DateFormatter::formatDate;

    @Test
    public void shouldFormatDates() {
      /*
        shouldFormat(-62135596800000L); // 00010101
        shouldFormat(-62135510399999L); // 00010102
        shouldFormat(238531025695285L); // 95280930
      */

        shouldFormat(MIN_TIMESTAMP);
        shouldFormat(MAX_TIMESTAMP);
        shouldFormat(3124193485230L);   // 20681231
    }

    @Test
    public void shouldFormatRandomDates() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(generateLong(MIN_TIMESTAMP, MAX_TIMESTAMP));
    }

    protected static void shouldFormat(long timestamp) {
        shouldFormat(timestamp, VERIFIER, FORMATTER);
    }


}
