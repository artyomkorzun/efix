package org.f1x.util.parse;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.BufferUtil;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class DoubleParserTest extends AbstractParserTest {

    protected static final Parser<Double> PARSER = DoubleParser::parseDouble;

    protected static final double[] PRECISION = {
            1E-1, 1E-2, 1E-3, 1E-4, 1E-5, 1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11, 1E-12, 1E-13, 1E-14
    };

    @Test
    public void shouldParseNumbers() {
        shouldParse("-1");
        shouldParse("-12");
        shouldParse("-123");
        shouldParse("-1234");
        shouldParse("-12345");
        shouldParse("-123456");
        shouldParse("-1234567");
        shouldParse("-12345678");
        shouldParse("-123456789");
        shouldParse("-1234567890");
        shouldParse("-12345678901");
        shouldParse("-123456789012");
        shouldParse("-1234567890123");
        shouldParse("-12345678901234");
        shouldParse("-123456789012345");
        shouldParse("-999999999999999");

        shouldParse("0");
        shouldParse("1");
        shouldParse("12");
        shouldParse("123");
        shouldParse("1234");
        shouldParse("12345");
        shouldParse("123456");
        shouldParse("1234567");
        shouldParse("12345678");
        shouldParse("123456789");
        shouldParse("1234567890");
        shouldParse("12345678901");
        shouldParse("123456789012");
        shouldParse("1234567890123");
        shouldParse("12345678901234");
        shouldParse("123456789012345");
        shouldParse("999999999999999");


        shouldParse("-1.23456789012345");
        shouldParse("-12.3456789012345");
        shouldParse("-123.456789012345");
        shouldParse("-1234.56789012345");
        shouldParse("-12345.6789012345");
        shouldParse("-123456.789012345");
        shouldParse("-1234567.89012345");
        shouldParse("-12345678.9012345");
        shouldParse("-123456789.012345");
        shouldParse("-1234567890.12345");
        shouldParse("-12345678901.2345");
        shouldParse("-123456789012.345");
        shouldParse("-1234567890123.45");
        shouldParse("-12345678901234.5");

        shouldParse("1.23456789012345");
        shouldParse("12.3456789012345");
        shouldParse("123.456789012345");
        shouldParse("1234.56789012345");
        shouldParse("12345.6789012345");
        shouldParse("123456.789012345");
        shouldParse("1234567.89012345");
        shouldParse("12345678.9012345");
        shouldParse("123456789.012345");
        shouldParse("1234567890.12345");
        shouldParse("12345678901.2345");
        shouldParse("123456789012.345");
        shouldParse("1234567890123.45");
        shouldParse("12345678901234.5");

        shouldParse("-0.23456789012345");
        shouldParse("-00.3456789012345");
        shouldParse("-000.456789012345");
        shouldParse("-0000.56789012345");
        shouldParse("-00000.6789012345");
        shouldParse("-000000.789012345");
        shouldParse("-0000000.89012345");
        shouldParse("-00000000.9012345");
        shouldParse("-000000000.012345");
        shouldParse("-0000000000.12345");
        shouldParse("-00000000000.2345");
        shouldParse("-000000000000.345");
        shouldParse("-0000000000000.45");
        shouldParse("-00000000000000.5");
        shouldParse("-00000000000000.0");

        shouldParse("0.23456789012345");
        shouldParse("00.3456789012345");
        shouldParse("000.456789012345");
        shouldParse("0000.56789012345");
        shouldParse("00000.6789012345");
        shouldParse("000000.789012345");
        shouldParse("0000000.89012345");
        shouldParse("00000000.9012345");
        shouldParse("000000000.012345");
        shouldParse("0000000000.12345");
        shouldParse("00000000000.2345");
        shouldParse("000000000000.345");
        shouldParse("0000000000000.45");
        shouldParse("00000000000000.5");
        shouldParse("00000000000000.0");

        shouldParse("-0.00000000000005");
        shouldParse("-00.0000000000005");
        shouldParse("-000.000000000005");
        shouldParse("-0000.00000000005");
        shouldParse("-00000.0000000005");
        shouldParse("-000000.000000005");
        shouldParse("-0000000.00000005");
        shouldParse("-00000000.0000005");
        shouldParse("-000000000.000005");
        shouldParse("-0000000000.00005");
        shouldParse("-00000000000.0005");
        shouldParse("-000000000000.005");
        shouldParse("-0000000000000.05");
        shouldParse("-00000000000000.5");

        shouldParse("0.00000000000005");
        shouldParse("00.0000000000005");
        shouldParse("000.000000000005");
        shouldParse("0000.00000000005");
        shouldParse("00000.0000000005");
        shouldParse("000000.000000005");
        shouldParse("0000000.00000005");
        shouldParse("00000000.0000005");
        shouldParse("000000000.000005");
        shouldParse("0000000000.00005");
        shouldParse("00000000000.0005");
        shouldParse("000000000000.005");
        shouldParse("0000000000000.05");
        shouldParse("00000000000000.5");

        shouldParse("-0.00000000000005");
        shouldParse("-00.0000000000005");
        shouldParse("-000.000000000005");
        shouldParse("-0000.00000000005");
        shouldParse("-00000.0000000005");
        shouldParse("-000000.000000005");
        shouldParse("-0000000.00000005");
        shouldParse("-00000000.0000005");
        shouldParse("-000000000.000005");
        shouldParse("-0000000000.00005");
        shouldParse("-00000000000.0005");
        shouldParse("-000000000000.005");
        shouldParse("-0000000000000.05");
        shouldParse("-00000000000000.5");

        shouldParse("0.");
        shouldParse("00.");
        shouldParse("000.");
        shouldParse("0000.");
        shouldParse("00000.");
        shouldParse("000000.");
        shouldParse("0000000.");
        shouldParse("00000000.");
        shouldParse("000000000.");
        shouldParse("0000000000.");
        shouldParse("00000000000.");
        shouldParse("000000000000.");
        shouldParse("0000000000000.");
        shouldParse("00000000000000.");
        shouldParse("000000000000000.");

        shouldParse("-0.");
        shouldParse("-00.");
        shouldParse("-000.");
        shouldParse("-0000.");
        shouldParse("-00000.");
        shouldParse("-000000.");
        shouldParse("-0000000.");
        shouldParse("-00000000.");
        shouldParse("-000000000.");
        shouldParse("-0000000000.");
        shouldParse("-00000000000.");
        shouldParse("-000000000000.");
        shouldParse("-0000000000000.");
        shouldParse("-00000000000000.");
        shouldParse("-000000000000000.");
    }

    @Test
    public void shouldParseRandomNumbers() {
        Random random = new Random();
        for (int i = 0; i < 50000; i++) {
            int integer = random.nextInt();
            int fractional = random.nextInt();
            String number = makeNumber(integer, fractional);
            shouldParse(number);
        }
    }

    @Test
    public void shouldFailParseNumbers() {
        shouldFailParse("0000000000000000=");
        shouldFailParse("00000000.00000000=");
        shouldFailParse("0000000000000000.=");
        shouldFailParse("-0000000.000000000=");
        shouldFailParse("-0000000000000000=");
        shouldFailParse("-0000000000000000.=");

        shouldFailParse("hd");
        shouldFailParse("3ttt");
        shouldFailParse("111");
        shouldFailParse("111111111111111111111111111111=");
        shouldFailParse("1");
        shouldFailParse("12345");
        shouldFailParse("-");
        shouldFailParse("-=");
        shouldFailParse("-123");
        shouldFailParse("=");
        shouldFailParse("");
    }

    protected static void shouldParse(String number) {
        Buffer buffer = BufferUtil.fromString(number + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        double expected = new BigDecimal(number).doubleValue();
        double actual = DoubleParser.parseDouble(SEPARATOR, buffer, offset, end);
        double precision = guessPrecision(number);

        assertEquals(end, offset.value());
        assertEquals(expected, actual, precision);
    }

    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

    protected static double guessPrecision(String number) {
        if (number.charAt(0) == '-')
            number = number.substring(1);

        int dotIndex = number.indexOf('.');
        if (dotIndex != -1)
            number = number.substring(0, dotIndex);

        return PRECISION[15 - number.length()];
    }

    protected static String makeNumber(int integer, int fractional) {
        StringBuilder builder = new StringBuilder();
        builder.append(integer);
        builder.append('.');
        builder.append(Math.abs(fractional));
        return builder.substring(0, Math.min(16, builder.length()));
    }

}
