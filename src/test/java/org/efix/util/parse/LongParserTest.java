package org.efix.util.parse;

import org.junit.Test;

import static org.efix.util.TestUtil.arrayOf;
import static org.efix.util.TestUtil.generateLong;

public class LongParserTest extends AbstractParserTest {

    protected static final Verifier<Long> VERIFIER = Long::parseLong;
    protected static final Parser<Long> INT_PARSER = LongParser::parseLong;
    protected static final Parser<Long> UINT_PARSER = LongParser::parseULong;
    protected static final Parser<Long>[] ALL_PARSERS = arrayOf(INT_PARSER, UINT_PARSER);

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
        shouldParse(-12345678901L, INT_PARSER);
        shouldParse(-123456789012L, INT_PARSER);
        shouldParse(-1234567890123L, INT_PARSER);
        shouldParse(-12345678901234L, INT_PARSER);
        shouldParse(-123456789012345L, INT_PARSER);
        shouldParse(-1234567890123456L, INT_PARSER);
        shouldParse(-12345678901234567L, INT_PARSER);
        shouldParse(-123456789012345678L, INT_PARSER);
        shouldParse(-999999999999999999L, INT_PARSER);

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
        shouldParse(12345678901L, ALL_PARSERS);
        shouldParse(123456789012L, ALL_PARSERS);
        shouldParse(1234567890123L, ALL_PARSERS);
        shouldParse(12345678901234L, ALL_PARSERS);
        shouldParse(123456789012345L, ALL_PARSERS);
        shouldParse(1234567890123456L, ALL_PARSERS);
        shouldParse(12345678901234567L, ALL_PARSERS);
        shouldParse(123456789012345678L, ALL_PARSERS);
        shouldParse(999999999999999999L, ALL_PARSERS);

        shouldParse("01", ALL_PARSERS);
        shouldParse("002", ALL_PARSERS);
        shouldParse("000101", ALL_PARSERS);
        shouldParse("00123456", ALL_PARSERS);
    }

    @Test
    public void shouldParseRandomNumbers() {
        for (int i = 0; i < 50000; i++) {
            long number = generateLong() / 10;
            if (number >= 0)
                shouldParse(number, ALL_PARSERS);
            else
                shouldParse(number, INT_PARSER);
        }
    }

    @Test
    public void shouldFailParseNumbers() {
        shouldFailParse(1000000000000000000L, ALL_PARSERS);
        shouldFailParse(Long.MAX_VALUE, ALL_PARSERS);
        shouldFailParse(-1000000000000000000L, ALL_PARSERS);
        shouldFailParse(Long.MIN_VALUE, ALL_PARSERS);

        shouldFailParse("hd", ALL_PARSERS);
        shouldFailParse("3ttt", ALL_PARSERS);
        shouldFailParse("111", ALL_PARSERS);
        shouldFailParse("111111111111111111111111111111=", ALL_PARSERS);
        shouldFailParse("1", ALL_PARSERS);
        shouldFailParse("12345", ALL_PARSERS);
        shouldFailParse("-", ALL_PARSERS);
        shouldFailParse("-=", ALL_PARSERS);
        shouldFailParse("-123", ALL_PARSERS);
        shouldFailParse("=", ALL_PARSERS);
        shouldFailParse("", ALL_PARSERS);
        shouldFailParse("0123456789011111111=", ALL_PARSERS);
        shouldFailParse("-0123456789011111111=", ALL_PARSERS);

        shouldFailParse("-1=", UINT_PARSER);
        shouldFailParse("-25=", UINT_PARSER);
        shouldFailParse("-105=", UINT_PARSER);
        shouldFailParse("-0105=", UINT_PARSER);
    }

    @SafeVarargs
    protected static void shouldParse(long number, Parser<Long>... parsers) {
        shouldParse("" + number, parsers);
    }

    @SafeVarargs
    protected static void shouldParse(String string, Parser<Long>... parsers) {
        shouldParse(string, VERIFIER, parsers);
    }

    protected static void shouldFailParse(long number, Parser<?>... parsers) {
        shouldFailParse("" + number + (char) SEPARATOR, parsers);
    }

}
