package org.f1x.util.format.newone;

import org.junit.Test;

import static org.f1x.util.TestUtil.arrayOf;
import static org.f1x.util.TestUtil.generateInt;

public class IntFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Integer> VERIFIER = Object::toString;

    protected static final Formatter<Integer> INT_FORMATTER = IntFormatter::formatInt;
    protected static final Formatter<Integer> UINT_FORMATTER = IntFormatter::formatUInt;
    protected static final Formatter<Integer>[] ALL_FORAMATTERS = arrayOf(INT_FORMATTER, UINT_FORMATTER);

    @Test
    public void shouldFormatNumbers() {
        shouldFormat(0, ALL_FORAMATTERS);
        shouldFormat(Integer.MAX_VALUE, ALL_FORAMATTERS);
        shouldFormat(Integer.MIN_VALUE, INT_FORMATTER);
    }

    @Test
    public void shouldFormatRandomNumbers() {
        for (int i = 0; i < 10000000; i++) {
            int value = generateInt();
            if (value >= 0)
                shouldFormat(value, ALL_FORAMATTERS);
            else
                shouldFormat(value, INT_FORMATTER);
        }
    }

    @Test
    public void shouldFailFormatNumbers() {
        shouldFailFormat(0, 1, 1, ALL_FORAMATTERS);
        shouldFailFormat(Integer.MAX_VALUE, 1, 10, ALL_FORAMATTERS);
        shouldFailFormat(Integer.MIN_VALUE, 1, 11, INT_FORMATTER);

        for (int valueLength = 1, value = 1; valueLength < 11; valueLength++, value *= 10) {
            shouldFailFormat(value, 1, valueLength, ALL_FORAMATTERS);
            shouldFailFormat(-value, 1, valueLength + 1, INT_FORMATTER);
        }
    }

    @SafeVarargs
    protected static void shouldFormat(int number, Formatter<Integer>... formatters) {
        shouldFormat(number, VERIFIER, formatters);
    }

}
