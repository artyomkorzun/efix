package org.f1x.util.parse;

import org.junit.Test;

import java.util.Random;

import static org.f1x.util.TestUtil.arrayOf;

public class IntParserTest extends AbstractParserTest {

    protected static final Verifier<Integer> VERIFIER = Integer::parseInt;
    protected static final Parser<Integer> INT_PARSER = IntParser::parseInt;
    protected static final Parser<Integer> UINT_PARSER = IntParser::parseUInt;
    protected static final Parser<Integer>[] ALL_PARSERS = arrayOf(INT_PARSER, UINT_PARSER);

    @Test
    public void shouldParseNumbers() {
        shouldParse(-1, INT_PARSER);
        shouldParse(-12, INT_PARSER);
        shouldParse(-123, INT_PARSER);
        shouldParse(-1234, INT_PARSER);
        shouldParse(-12345, INT_PARSER);
        shouldParse(-123456, INT_PARSER);
        shouldParse(-1234567, INT_PARSER);
        shouldParse(-12345678, INT_PARSER);
        shouldParse(-123456789, INT_PARSER);
        shouldParse(-1234567890, INT_PARSER);
        shouldParse(-Integer.MAX_VALUE, INT_PARSER);

        shouldParse("-00012", INT_PARSER);
        shouldParse("-031", INT_PARSER);

        shouldParse(0, ALL_PARSERS);
        shouldParse(1, ALL_PARSERS);
        shouldParse(12, ALL_PARSERS);
        shouldParse(123, ALL_PARSERS);
        shouldParse(1234, ALL_PARSERS);
        shouldParse(12345, ALL_PARSERS);
        shouldParse(123456, ALL_PARSERS);
        shouldParse(1234567, ALL_PARSERS);
        shouldParse(12345678, ALL_PARSERS);
        shouldParse(123456789, ALL_PARSERS);
        shouldParse(1234567890, ALL_PARSERS);
        shouldParse(Integer.MAX_VALUE, ALL_PARSERS);

        shouldParse("01", ALL_PARSERS);
        shouldParse("002", ALL_PARSERS);
        shouldParse("000101", ALL_PARSERS);
        shouldParse("00123456", ALL_PARSERS);
    }

    @Test
    public void shouldParseRandomNumbers() {
        Random random = new Random();
        for (int i = 0; i < 50000; i++) {
            int number = random.nextInt();
            if (number >= 0)
                shouldParse(number, ALL_PARSERS);
            else
                shouldParse(number, INT_PARSER);
        }
    }

    @Test
    public void shouldFailParseNumbers() {
        shouldFailParse(makeString(1L + Integer.MAX_VALUE), ALL_PARSERS);
        shouldFailParse(makeString(-1L + Integer.MIN_VALUE), ALL_PARSERS);
        shouldFailParse("hd", ALL_PARSERS);
        shouldFailParse("3ttt", ALL_PARSERS);
        shouldFailParse("111", ALL_PARSERS);
        shouldFailParse("1111111111111111111=", ALL_PARSERS);
        shouldFailParse("1", ALL_PARSERS);
        shouldFailParse("12345", ALL_PARSERS);
        shouldFailParse("-", ALL_PARSERS);
        shouldFailParse("-=", ALL_PARSERS);
        shouldFailParse("-123", ALL_PARSERS);
        shouldFailParse("=", ALL_PARSERS);
        shouldFailParse("", ALL_PARSERS);
        shouldFailParse("01234567890=", ALL_PARSERS);
        shouldFailParse("-01234567890=", ALL_PARSERS);

        shouldFailParse("-1=", UINT_PARSER);
        shouldFailParse("-25=", UINT_PARSER);
        shouldFailParse("-105=", UINT_PARSER);
        shouldFailParse("-0105=", UINT_PARSER);
    }

    @SafeVarargs
    protected static void shouldParse(int number, Parser<Integer>... parsers) {
        shouldParse("" + number, parsers);
    }

    @SafeVarargs
    protected static void shouldParse(String string, Parser<Integer>... parsers) {
        shouldParse(string, VERIFIER, parsers);
    }

    protected static String makeString(long value) {
        return "" + value + (char) SEPARATOR;
    }

}
