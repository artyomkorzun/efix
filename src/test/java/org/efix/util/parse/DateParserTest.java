package org.efix.util.parse;

import org.efix.util.TestUtil;
import org.junit.Test;

public class DateParserTest extends AbstractParserTest {

    protected static final Verifier<Long> VERIFIER = TestUtil::parseDate;
    protected static final Parser<Long> PARSER = DateParser::parseDate;

    protected static final int[] DAYS_IN_MONTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    protected static final int[] DAYS_IN_MONTH_LEAP = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Test
    public void shouldParseDates() {
        for (int year = 0; year <= 9999; year++) {
            boolean leapYear = (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0);
            int[] daysInMonth = leapYear ? DAYS_IN_MONTH_LEAP : DAYS_IN_MONTH;

            for (int month = 1; month <= 12; month++) {
                for (int day = 1; day <= daysInMonth[month]; day++)
                    shouldParse(String.format("%04d%02d%02d", year, month, day));
            }
        }
    }

    @Test
    public void shouldFailParseDates() {
        shouldFailParse("19700101");
        shouldFailParse("19700101|");
        shouldFailParse("1970010=");
        shouldFailParse("s9700101=");
        shouldFailParse("1s700101=");
        shouldFailParse("11s00101=");
        shouldFailParse("111-0101=");
        shouldFailParse("11110001=");
        shouldFailParse("11111000=");
        shouldFailParse("00010229=");
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
