package org.efix.util.parse.exp;

import org.efix.util.TestUtil;
import org.efix.util.parse.TimestampParser;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.efix.util.TestUtil.generateInt;


public class TimestampParserTest extends AbstractParserTest {

    protected static final Verifier<Long> VERIFIER = TestUtil::parseTimestampMs;
    protected static final Parser<Long> PARSER = TimestampParser::parseTimestamp;

    protected static final Verifier<Long> VERIFIER_S = TestUtil::parseTimestampS;
    protected static final Parser<Long> PARSER_S = (tag, buffer, offset, end) -> TimestampParser.parseTimestamp(tag, TimeUnit.SECONDS, buffer, offset, end);

    protected static final Verifier<Long> VERIFIER_MS = TestUtil::parseTimestampMs;
    protected static final Parser<Long> PARSER_MS = (tag, buffer, offset, end) -> TimestampParser.parseTimestamp(tag, TimeUnit.MILLISECONDS, buffer, offset, end);

    protected static final Verifier<Long> VERIFIER_US = TestUtil::parseTimestampUs;
    protected static final Parser<Long> PARSER_US = (tag, buffer, offset, end) -> TimestampParser.parseTimestamp(tag, TimeUnit.MICROSECONDS, buffer, offset, end);

    protected static final Verifier<Long> VERIFIER_NS = TestUtil::parseTimestampNs;
    protected static final Parser<Long> PARSER_NS = (tag, buffer, offset, end) -> TimestampParser.parseTimestamp(tag, TimeUnit.NANOSECONDS, buffer, offset, end);

    @Test
    public void shouldParseTimestamps() {
        for (int i = 0; i < 1000000; i++) {
            int year = generateInt(2000, 2099);
            int month = generateInt(1, 12);
            int day = generateInt(1, 28);
            int hour = generateInt(0, 23);
            int minute = generateInt(0, 59);
            int second = generateInt(0, 59);
            int millisecond = generateInt(0, 999);
            int nanosecond = ((i & 1) == 0) ? generateInt(0, 999999) : generateInt(0, 999_999_999);

            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d", year, month, day, hour, minute, second));
            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millisecond));
            shouldParseWithTimeUnit(String.format("%04d%02d%02d-%02d:%02d:%02d.%d", year, month, day, hour, minute, second, nanosecond));
        }
    }

    @Test
    public void shouldFailParse() {
        shouldFailParse("00010101x00:00:01=");
        shouldFailParse("=");
        shouldFailParse("");
    }

    protected static void shouldParse(String string) {
        shouldParse(string, VERIFIER, PARSER);
        shouldParseWithTimeUnit(string);
    }

    private static void shouldParseWithTimeUnit(final String string) {
        shouldParse(string, VERIFIER_S, PARSER_S);
        shouldParse(string, VERIFIER_MS, PARSER_MS);
        shouldParse(string, VERIFIER_US, PARSER_US);
        shouldParse(string, VERIFIER_NS, PARSER_NS);
    }

    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

}
