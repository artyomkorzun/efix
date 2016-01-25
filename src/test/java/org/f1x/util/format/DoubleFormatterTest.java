package org.f1x.util.format;

import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.type.DoubleType;
import org.junit.Test;

import java.math.BigDecimal;

import static org.f1x.util.TestUtil.generateDouble;
import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void shouldFormatRandomDoubles() {
        for (int i = 0; i < 10000000; i++) {
            double value = generateDouble(DoubleType.MIN_VALUE, DoubleType.MAX_VALUE);
            int digits = digits((long) value);
            shouldFormat(value, 15 - digits);
            shouldFormat(value, 0);
        }
    }

    protected static void shouldFormat(double value, int precision) {
        shouldFormat(value, precision, true);
        shouldFormat(value, precision, false);
    }

    protected static void shouldFormat(double value, int precision, boolean roundUp) {
        MutableBuffer buffer = new UnsafeBuffer(new byte[DoubleFormatter.MAX_LENGTH]);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        DoubleFormatter.formatDouble(value, precision, roundUp, buffer, offset, end);
        String actual = new ByteSequenceWrapper().wrap(buffer, 0, offset.get()).toString();
        String expected = verifier(value, precision, roundUp);

        String message = String.format("Fail to parse double %s, precision %s", value, precision);
        assertEquals(message, expected, actual);
    }

    protected static int digits(long value) {
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
                .setScale(precision < 0 ? 0 : precision, roundUp ? BigDecimal.ROUND_HALF_UP : BigDecimal.ROUND_HALF_DOWN)
                .stripTrailingZeros()
                .toPlainString();
    }

}
