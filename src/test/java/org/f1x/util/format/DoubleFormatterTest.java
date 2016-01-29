package org.f1x.util.format;

import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.type.DoubleType;
import org.junit.Test;

import java.math.BigDecimal;

import static org.f1x.util.TestUtil.generateDouble;
import static org.junit.Assert.*;


public class DoubleFormatterTest extends AbstractFormatterTest {

    @Test
    public void shouldFormatDoubles() {
        shouldFormat(0.0, 0);
        shouldFormat(0.0, 1);
        shouldFormat(-0.0, 0);
        shouldFormat(-0.0, 1);
        shouldFormat(DoubleType.MAX_VALUE, 0);
        shouldFormat(DoubleType.MIN_VALUE, 0);
        shouldFormat(99999999999999.5, 1);
        shouldFormat(-99999999999999.5, 1);
        shouldFormat(99999999999999.140625, 1);
        shouldFormat(99999999999999.08999, 1);
        shouldFormat(-9.911900025316275E14, 0);
        shouldFormat(10.99, 1);
        shouldFormat(99.99, 1);
        shouldFormat(10.095, 2);
    }

    @Test
    public void shouldFormatRandomDoubles() {
        for (int i = 0; i < 10000000; i++) {
            double value = generateDouble(DoubleType.MIN_VALUE, DoubleType.MAX_VALUE);
            int maxPrecision = maxPrecision(value);

            for (int precision = -1; precision <= maxPrecision; precision++)
                shouldFormat(value, precision);

        }
    }

    @Test
    public void shouldFailFormatDoublesOutOfRange() {
        shouldFailFormat(Double.NaN);
        shouldFailFormat(Double.NEGATIVE_INFINITY);
        shouldFailFormat(Double.POSITIVE_INFINITY);
        shouldFailFormat(DoubleType.MIN_VALUE - 1);
        shouldFailFormat(DoubleType.MAX_VALUE + 1);
    }

    protected static void shouldFormat(double value, int precision) {
        shouldFormat(value, precision, true);
        shouldFormat(value, precision, false);
    }

    protected static void shouldFormat(double value, int precision, boolean roundUp) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(DoubleType.MAX_LENGTH);
        int length = DoubleFormatter.formatDouble(value, precision, roundUp, buffer, 0);

        String actual = BufferUtil.toString(buffer, 0, length);
        String expected = verifier(value, precision, roundUp);

        String message = String.format("Fail to format double %s with precision %s", value, precision);
        assertEquals(message, expected, actual);
    }

    protected static void shouldFailFormat(double value) {
        for (int precision = -1; precision <= DoubleFormatter.MAX_PRECISION; precision++) {
            shouldFailFormat(value, precision, false);
            shouldFailFormat(value, precision, true);
        }
    }

    protected static void shouldFailFormat(double value, int precision, boolean roundUp) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(DoubleType.MAX_LENGTH);

        try {
            DoubleFormatter.formatDouble(value, precision, roundUp, buffer, 0);
            fail("Should fail to format " + value);
        } catch (FormatterException e) {
            assertTrue(true);
        }
    }

    protected static int maxPrecision(double value) {
        return DoubleFormatter.MAX_PRECISION - digits(value);
    }

    protected static int digits(double value) {
        value = Math.abs(value);
        long p = 10;
        for (int i = 1; i < 19; i++) {
            if (value < p)
                return i;

            p = 10 * p;
        }

        return 19;
    }

    protected static String verifier(double value, int precision, boolean roundUp) {
        return new BigDecimal(value)
                .setScale(Math.max(0, precision), roundUp ? BigDecimal.ROUND_HALF_UP : BigDecimal.ROUND_HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

}
