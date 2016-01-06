package org.f1x.util.parse;

import org.f1x.util.TestUtil;
import org.junit.Test;

import static org.f1x.util.TestUtil.generateInt;

public class TimestampParserTest extends AbstractParserTest {

    protected static final Verifier<Long> VERIFIER = TestUtil::parseTimestamp;
    protected static final Parser<Long> PARSER = TimestampParser::parseTimestamp;

    @Test
    public void shouldParseTimestamps() {
        for (int i = 0; i < 1000000; i++) {
            int year = generateInt(0, 9999);
            int month = generateInt(1, 12);
            int day = generateInt(1, 28);
            int hour = generateInt(0, 23);
            int minute = generateInt(0, 59);
            int second = generateInt(0, 59);
            int millisecond = generateInt(0, 999);

            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d", year, month, day, hour, minute, second));
            shouldParse(String.format("%04d%02d%02d-%02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millisecond));
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
    }

    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

}
