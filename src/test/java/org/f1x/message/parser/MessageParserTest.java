package org.f1x.message.parser;

import org.f1x.util.StringUtil;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.f1x.util.TestUtil.*;
import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class MessageParserTest {

    @Parameters(name = "{0}")
    public static Collection<MessageParser> parameters() {
        return Arrays.asList(new FastMessageParser(), new OptimizedMessageParser());
    }

    protected final MessageParser parser;

    public MessageParserTest(MessageParser parser) {
        this.parser = parser;
    }

    @Test
    public void testLogon() {
        assertParser("8=FIX.4.4|9=116|35=A|34=1|49=DEMO2Kweoj_DEMOFIX|52=20121009-13:14:57.089|56=DUKASCOPYFIX|98=0|108=30|141=Y|553=DEMO2Kweoj|554=**********|10=202|");
    }

    @Test
    public void testNewOrderSingle() {
        assertParser("8=FIX.4.4|9=144|35=D|34=6|49=DEMO2Kweoj_DEMOFIX|52=20121009-13:59:01.666|56=DUKASCOPYFIX|11=512|21=1|38=1000|40=1|54=1|55=EUR/USD|59=1|60=20121009-13:59:01.666|10=000|");
    }

    @Test
    public void testExecutionReport() {
        assertParser("8=FIX.4.4|9=251|35=8|34=7|49=DUKASCOPYFIX|52=20121009-13:59:21.158|56=DEMO2Kweoj_DEMOFIX|6=0|11=506|14=0|17=506|37=506|38=0|39=8|54=7|55=UNKNOWN|58=Your order has been rejected due to validation failure.  Order amount can't be less than <MIN_OPEN_AMOUNT>|150=8|151=0|10=196|");
    }

    @Test
    public void testFieldTypes() {
        String message = "1=ABC|2=123|3=3.14159|4=x|5=Y|6=N|7=20121009-13:44:49.421|8=20121009|9=13:44:49.421|10=20121122|";
        int tags = 0;
        parser.wrap(buffer(message));
        while (parser.next()) {
            tags++;
            switch (parser.tag()) {
                case 1:
                    assertEquals("ABC", parser.string());
                    break;
                case 2:
                    assertEquals(123, parser.longValue());
                    assertEquals(123, parser.intValue());
                    break;
                case 3:
                    assertEquals(3.14159, parser.doubleValue(), 0.000001);
                    break;
                case 4:
                    assertEquals('x', parser.byteValue());
                    break;
                case 5:
                    assertTrue(parser.booleanValue());
                    break;
                case 6:
                    assertFalse(parser.booleanValue());
                    break;
                case 7:
                    assertEquals(parseUTCTimestamp("20121009-13:44:49.421"), parser.utcTimestamp());
                    break;
                case 8:
                    assertEquals(parseUTCDate("20121009"), parser.utcDate());
                    break;
                case 9:
                    assertEquals(parseUTCTime("13:44:49.421"), parser.utcTime());
                    break;
                case 10:
                    assertEquals(parseLocalDate("20121122"), parser.localDate());
                    break;
                default:
                    fail("unexpected field:" + parser.tag());
            }
        }

        assertEquals(10, tags);
    }

    protected void assertParser(String message) {
        Buffer buffer = buffer(message);
        parser.wrap(buffer);

        StringBuilder builder = new StringBuilder(message.length());
        while (parser.next()) {
            builder.append(parser.tag());
            builder.append('=');
            builder.append(parser.charSequence());
            builder.append('|');
        }

        assertEquals(message, builder.toString());
    }

    protected static Buffer buffer(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

}
