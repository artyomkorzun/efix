package org.efix.util.parse.exp;

import org.efix.util.TestUtil;
import org.efix.util.parse.TimestampParser;
import org.junit.Test;

import static org.efix.util.TestUtil.generateInt;


public class TimestampParserTest extends AbstractParserTest {

    protected static final Verifier<Long> VERIFIER = TestUtil::parseTimestampMs;
    protected static final Parser<Long> PARSER = TimestampParser::parseTimestampMs;

    protected static final Verifier<Long> VERIFIER_NS = TestUtil::parseTimestampNs;
    protected static final Parser<Long> PARSER_NS = TimestampParser::parseTimestampNs;

    @Test
    public void shouldParse() {
        shouldParse("20331216-16:50:39.851");
    }

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
            int microsecond = generateInt(0, 999);
            int nanosecond = generateInt(0, 999);

            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d", year, month, day, hour, minute, second));
            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millisecond));
            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d.%03d%03d", year, month, day, hour, minute, second, millisecond, microsecond));
            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d.%03d%03d%03d", year, month, day, hour, minute, second, millisecond, microsecond, nanosecond));
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
        shouldParse(string, VERIFIER_NS, PARSER_NS);
    }


    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

}
