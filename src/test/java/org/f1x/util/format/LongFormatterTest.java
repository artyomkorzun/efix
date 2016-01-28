package org.f1x.util.format;

import org.junit.Test;

import static org.f1x.util.TestUtil.generateLong;


public class LongFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Long> VERIFIER = Object::toString;
    protected static final Formatter<Long> FORMATTER = LongFormatter::formatLong;

    @Test
    public void shouldFormatNumbers() {
        shouldFormat(0);
        shouldFormat(Long.MAX_VALUE);
        shouldFormat(Long.MIN_VALUE);
    }

    @Test
    public void shouldFormatRandomNumbers() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(generateLong());
    }

    protected static void shouldFormat(long number) {
        shouldFormat(number, VERIFIER, FORMATTER);
    }

}
