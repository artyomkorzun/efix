package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.TestUtil;
import org.f1x.util.buffer.Buffer;
import org.junit.Test;

import static org.f1x.util.TestUtil.makeMessage;
import static org.junit.Assert.assertEquals;

public class TimeParserTest extends AbstractParserTest {

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
    }

    protected static void shouldParse(String time) {
        Buffer buffer = makeMessage(time + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        int actual = TimeParser.parseTime(SEPARATOR, buffer, offset, end);
        int expected = TestUtil.parseUTCTime(time);

        assertEquals(end, offset.value());
        assertEquals(time, expected, actual);
    }

    protected static void shouldFailParse(String string) {
        Buffer buffer = makeMessage(string);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();
        failIfParsed(string, () -> TimeParser.parseTime(SEPARATOR, buffer, offset, end));
    }

}
