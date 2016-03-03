package org.efix.util.parse;

import org.efix.util.TestUtil;
import org.junit.Test;

public class TimeParserTest extends AbstractParserTest {

    protected static final Verifier<Integer> VERIFIER = TestUtil::parseTime;
    protected static final Parser<Integer> PARSER = TimeParser::parseTime;

    @Test
    public void shouldParseTimes() {
        for (int hour = 0, millisecond = 0; hour <= 23; hour++) {
            for (int minute = 0; minute <= 59; minute++) {
                for (int second = 0; second <= 59; second++, millisecond = ++millisecond % 1000) {
                    shouldParse(String.format("%02d:%02d:%02d", hour, minute, second));
                    shouldParse(String.format("%02d:%02d:%02d.%03d", hour, minute, second, millisecond));
                }
            }
        }
    }

    @Test
    public void shouldFailParseTimes() {
        shouldFailParse("10-10:10=");
        shouldFailParse("10:10-10=");
        shouldFailParse("10:10:10-");
        shouldFailParse("24:10:10=");
        shouldFailParse("01:60:10=");
        shouldFailParse("01:10:61=");
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
