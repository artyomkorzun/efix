package org.efix.util.format;

import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.efix.util.type.DecimalType;
import org.junit.Test;

import java.math.BigDecimal;

import static org.efix.util.TestUtil.generateLong;
import static org.efix.util.format.LongFormatter.ulongLength;
import static org.junit.Assert.*;


public class DecimalFormatterTest {

    public static final long MAX = 999999999999999L;
    public static final long MIN = -999999999999999L;

    @Test
    public void shouldFormatDecimals() {
        shouldFormat(0, 0);
        shouldFormat(0, 1);
        shouldFormat(0, 18);
        shouldFormat(MAX, 0);
        shouldFormat(MIN, 0);
        shouldFormat(9999999999999990L, 1);
        shouldFormat(-9999999999999990L, 1);
        shouldFormat(1099, 2);
        shouldFormat(9999, 3);
        shouldFormat(10095, 3);
        shouldFormat(10095, 4);
        shouldFormat(1, 14);
    }

    @Test
    public void shouldFormatRandomDecimals() {
        for (int i = 0; i < 20000000; i++) {
            long decimal = generateLong(0, MAX);
            int maxScale = DecimalType.MAX_DIGITS - ulongLength(decimal);

            for (int scale = 0; scale <= maxScale; scale++) {
                shouldFormat(decimal, scale);
                shouldFormat(-decimal, scale);
            }
        }
    }

    @Test
    public void shouldFailFormatDecimals() {
        shouldFormat(MAX + 1, 0);
        shouldFormat(MIN - 1, 0);
        shouldFormat(9999999999999991L, 1);
        shouldFormat(-9999999999999991L, 1);
        shouldFormat(1234567890123456L, 18);
        shouldFormat(1, 15);
        shouldFormat(Long.MAX_VALUE, 0);
        shouldFormat(Long.MIN_VALUE, 0);
    }

    protected static void shouldFormat(long decimal, int scale) {
        MutableBuffer buffer = UnsafeBuffer.allocateDirect(30);
        String expected = verifier(decimal, scale);
        int length = DecimalFormatter.formatDecimal(decimal, scale, buffer, 0);
        String actual = BufferUtil.toString(buffer, 0, length);

        assertEquals("Decimal mismatches", expected, actual);
    }

    protected static void shouldFailFormat(long decimal, int scale) {
        MutableBuffer buffer = UnsafeBuffer.allocateDirect(32);

        try {
            DecimalFormatter.formatDecimal(decimal, scale, buffer, 0);
            fail(String.format("Should fail format decimal %s with scale %s", decimal, scale));
        } catch (IllegalArgumentException e) {
            assertTrue("Caught", true);
        }
    }

    protected static String verifier(long decimal, int scale) {
        return BigDecimal.valueOf(decimal, scale).stripTrailingZeros().toPlainString();
    }

}
