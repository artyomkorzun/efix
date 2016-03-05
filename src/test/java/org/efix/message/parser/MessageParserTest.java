package org.efix.message.parser;

import org.junit.Test;

import static org.efix.util.TestUtil.*;
import static org.junit.Assert.*;

public class MessageParserTest {

    @Test
    public void shouldParseLogon() {
        assertParser("8=FIX.4.4|9=116|35=A|34=1|49=DEMO2Kweoj_DEMOFIX|52=20121009-13:14:57.089|56=DUKASCOPYFIX|98=0|108=30|141=Y|553=DEMO2Kweoj|554=**********|10=202|");
    }

    @Test
    public void shouldParseNewOrderSingle() {
        assertParser("8=FIX.4.4|9=144|35=D|34=6|49=DEMO2Kweoj_DEMOFIX|52=20121009-13:59:01.666|56=DUKASCOPYFIX|11=512|21=1|38=1000|40=1|54=1|55=EUR/USD|59=1|60=20121009-13:59:01.666|10=000|");
    }

    @Test
    public void shouldParseExecutionReport() {
        assertParser("8=FIX.4.4|9=251|35=8|34=7|49=DUKASCOPYFIX|52=20121009-13:59:21.158|56=DEMO2Kweoj_DEMOFIX|6=0|11=506|14=0|17=506|37=506|38=0|39=8|54=7|55=UNKNOWN|58=Your order has been rejected due to validation failure.  Order amount can't be less than <MIN_OPEN_AMOUNT>|150=8|151=0|10=196|");
    }

    @Test
    public void shouldParseFields() {
        String message = "1=charSequence|2=12345|3=123456789012345|4=3.14159|5=b|6=Y|7=20121009-13:44:49.421|8=20121009|9=13:44:49.421|10=skipped value|11=byteSequence|12=0.0001|";
        MessageParser parser = new FastMessageParser();
        parser.wrap(byteMessage(message));

        int tags = 0;
        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case 1:
                    assertEquals("charSequence", parser.parseCharSequence().toString());
                    break;
                case 2:
                    assertEquals(12345, parser.parseInt());
                    break;
                case 3:
                    assertEquals(123456789012345L, parser.parseLong());
                    break;
                case 4:
                    assertEquals(3.14159, parser.parseDouble(), 0.0000000001);
                    break;
                case 5:
                    assertEquals('b', parser.parseByte());
                    break;
                case 6:
                    assertTrue(parser.parseBoolean());
                    break;
                case 7:
                    assertEquals(parseTimestamp("20121009-13:44:49.421"), parser.parseTimestamp());
                    break;
                case 8:
                    assertEquals(parseDate("20121009"), parser.parseDate());
                    break;
                case 9:
                    assertEquals(parseTime("13:44:49.421"), parser.parseTime());
                    break;
                case 10:
                    parser.parseValue();
                    break;
                case 11:
                    assertEquals("byteSequence", parser.parseByteSequence().toString());
                    break;
                case 12:
                    assertEquals(1, parser.parseDecimal(4));
                    break;
                default:
                    fail("unexpected field " + tag);
            }

            tags++;
        }

        assertEquals(12, tags);
    }

    @Test
    public void shouldParseAfterReset() {
        String message = "1=ABC|2=12345|3=123456789012345|";
        MessageParser parser = new FastMessageParser();
        parser.wrap(byteMessage(message));

        assertParser(message, parser);
        parser.reset();
        assertParser(message, parser);
    }

    @Test
    public void shouldParseMiddleOfMessage() {
        String expected = "3=3|4=4|";
        String message = "1=1|2=2|" + expected + "5=5|6=6|";

        MessageParser parser = new FastMessageParser();
        parser.wrap(byteMessage(message), 8, 8);

        assertParser(expected, parser);
        parser.reset();
        assertParser(expected, parser);
    }

    protected void assertParser(String message) {
        MessageParser parser = new FastMessageParser();
        parser.wrap(byteMessage(message));
        assertParser(message, parser);
    }

    private void assertParser(String expected, MessageParser parser) {
        StringBuilder builder = new StringBuilder(expected.length());
        while (parser.hasRemaining()) {
            builder.append(parser.parseTag());
            builder.append('=');
            builder.append(parser.parseCharSequence());
            builder.append('|');
        }

        assertEquals(expected, builder.toString());
    }

}
