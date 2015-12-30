package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.TestUtil;
import org.f1x.util.buffer.Buffer;
import org.junit.Test;

import static org.f1x.util.TestUtil.makeMessage;
import static org.junit.Assert.assertEquals;

public class DateParserTest extends AbstractParserTest {

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
    }

    protected static void shouldParse(String date) {
        Buffer buffer = makeMessage(date + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        long actual = DateParser.parseDate(SEPARATOR, buffer, offset, end);
        long expected = TestUtil.parseUTCDate(date);

        assertEquals(end, offset.value());
        assertEquals(date, expected, actual);
    }

    protected static void shouldFailParse(String string) {
        Buffer buffer = makeMessage(string);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();
        failIfParsed(string, () -> DateParser.parseDate(SEPARATOR, buffer, offset, end));
    }

}
