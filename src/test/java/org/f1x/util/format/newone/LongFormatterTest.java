package org.f1x.util.format.newone;

import org.f1x.util.TestUtil;
import org.junit.Test;

import static org.f1x.util.TestUtil.arrayOf;

public class LongFormatterTest extends AbstractFormatterTest {


    protected static final Verifier<Long> VERIFIER = Object::toString;

    protected static final Formatter<Long> INT_FORMATTER = LongFormatter::formatLong;
    protected static final Formatter<Long> UINT_FORMATTER = LongFormatter::formatULong;
    protected static final Formatter<Long>[] ALL_FORAMATTERS = arrayOf(INT_FORMATTER, UINT_FORMATTER);

    @Test
    public void shouldFormatNumbers() {
        shouldFormat(0, ALL_FORAMATTERS);
        shouldFormat(Long.MAX_VALUE, ALL_FORAMATTERS);
        shouldFormat(Long.MIN_VALUE, INT_FORMATTER);
    }

    @Test
    public void shouldFormatRandomNumbers() {
        for (int i = 0; i < 10000000; i++) {
            long value = TestUtil.generateLong();
            if (value >= 0)
                shouldFormat(value, ALL_FORAMATTERS);
            else
                shouldFormat(value, INT_FORMATTER);
        }
    }

    @Test
    public void shouldFailFormatNumbers() {
        shouldFailFormat(0L, 1, 1, ALL_FORAMATTERS);
        shouldFailFormat(Long.MAX_VALUE, 1, 19, ALL_FORAMATTERS);
        shouldFailFormat(Long.MIN_VALUE, 1, 20, INT_FORMATTER);

        long value = 1;
        for (int valueLength = 1; valueLength < 20; valueLength++, value *= 10) {
            shouldFailFormat(value, 1, valueLength, ALL_FORAMATTERS);
            shouldFailFormat(-value, 1, valueLength + 1, INT_FORMATTER);
        }
    }

    @SafeVarargs
    protected static void shouldFormat(long number, Formatter<Long>... formatters) {
        shouldFormat(number, VERIFIER, formatters);
    }

}
