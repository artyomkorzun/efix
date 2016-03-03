package org.efix.util.format;

import org.junit.Test;

import static org.efix.util.TestUtil.generateInt;


public class IntFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Integer> VERIFIER = Object::toString;
    protected static final Formatter<Integer> FORMATTER = IntFormatter::formatInt;

    @Test
    public void shouldFormatNumbers() {
        shouldFormat(0);
        shouldFormat(Integer.MAX_VALUE);
        shouldFormat(Integer.MIN_VALUE);
    }

    @Test
    public void shouldFormatRandomNumbers() {
        for (int i = 0; i < 10000000; i++)
            shouldFormat(generateInt());
    }

    protected static void shouldFormat(int number) {
        shouldFormat(number, VERIFIER, FORMATTER);
    }

}
