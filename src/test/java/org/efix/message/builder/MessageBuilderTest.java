package org.efix.message.builder;

import org.efix.message.field.Tag;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import static org.efix.util.StringUtil.asciiBytes;
import static org.efix.util.TestUtil.*;
import static org.efix.util.buffer.BufferUtil.fromString;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class MessageBuilderTest {

    protected final MessageBuilder builder;

    public MessageBuilderTest(MessageBuilder builder) {
        this.builder = builder;
    }

    @Test
    public void shouldBuildLogon() {
        String expected = "8=FIX.4.4|9=116|35=A|34=1|49=DEMO2Kweoj_DEMOFIX|52=20121009-13:14:57.089|56=DUKASCOPYFIX|98=0|108=30|141=Y|553=username|554=password|10=202|";

        MutableBuffer buffer = new UnsafeBuffer(new byte[1024]);
        builder.wrap(buffer)
                .addCharSequence(Tag.BeginString, "FIX.4.4")
                .addInt(Tag.BodyLength, 116)
                .addByte(Tag.MsgType, (byte) 'A')
                .addLong(Tag.MsgSeqNum, 1L)
                .addBytes(Tag.SenderCompID, asciiBytes("DEMO2Kweoj_DEMOFIX"))
                .addTimestampMs(Tag.SendingTime, parseTimestampMs("20121009-13:14:57.089"))
                .addBytes(Tag.TargetCompID, fromString("DUKASCOPYFIX"))
                .addDouble(Tag.EncryptMethod, 0.0, 0)
                .addDouble(Tag.HeartBtInt, 30.0, 1, false)
                .addBoolean(Tag.ResetSeqNumFlag, true)
                .addCharSequence(Tag.Username, "-username-", 1, 8)
                .addByteSequence(Tag.Password, new ByteSequenceWrapper(fromString("-password-")), 1, 8)
                .addInt(Tag.CheckSum, 202);

        String actual = BufferUtil.toString(buffer, 0, builder.length());
        assertMessage(expected, actual);
    }

    @Test
    public void shouldBuildMessage() {
        String expected = "1=Y|2=b|3=c|4=4|5=5|6=6.1|7=7.2|8=20000101-00:00:00.000|9=00:00:00.000|10=20000101|" +
                "11=array|12=buffer|13=byte sequence|14=char sequence|15=N|16=b|17=c|18=812|19=923123|20=3.14|" +
                "21=3.15|22=20000101-00:00:00.000|23=23:59:59.000|24=20000101|25=array|26=buffer|27=sequence|28=sequence|29=0.0001|30=10.099";

        MutableBuffer buffer = new UnsafeBuffer(new byte[1024]);
        builder.wrap(buffer)
                .addBoolean(1, true)
                .addByte(2, (byte) 'b')
                .addChar(3, 'c')
                .addInt(4, 4)
                .addLong(5, 5L)
                .addDouble(6, 6.1, 2)
                .addDouble(7, 7.2, 2, false)
                .addTimestampMs(8, parseTimestampMs("20000101-00:00:00"))
                .addTime(9, parseTime("00:00:00"))
                .addDate(10, parseDate("20000101"))
                .addBytes(11, asciiBytes("array"))
                .addBytes(12, fromString("buffer"))
                .addByteSequence(13, new ByteSequenceWrapper(fromString("byte sequence")))
                .addCharSequence(14, "char sequence")
                .startField(15).appendBoolean(false).endField()
                .startField(16).appendByte((byte) 'b').endField()
                .startField(17).appendChar('c').endField()
                .startField(18).appendInt(812).endField()
                .startField(19).appendLong(923123L).endField()
                .startField(20).appendDouble(3.14, 2).endField()
                .startField(21).appendDouble(3.15, 2, false).endField()
                .startField(22).appendTimestampMs(parseTimestampMs("20000101-00:00:00")).endField()
                .startField(23).appendTime(parseTime("23:59:59")).endField()
                .startField(24).appendDate(parseDate("20000101")).endField()
                .startField(25).appendBytes(asciiBytes("array")).endField()
                .startField(26).appendBytes(fromString("buffer")).endField()
                .startField(27).appendByteSequence(new ByteSequenceWrapper(fromString("sequence"))).endField()
                .startField(28).appendCharSequence("sequence").endField()
                .addDecimal(29, 1, 4)
                .startField(30).appendDecimal(10099, 3);

        String actual = BufferUtil.toString(buffer, 0, builder.length());
        assertMessage(expected, actual);
    }

    protected static void assertMessage(String expected, String actual) {
        expected = stringMessage(expected);
        assertEquals("Fail to build " + expected, expected, actual);
    }

    @Parameters(name = "{0}")
    public static List<MessageBuilder> builders() {
        FastMessageBuilder builder = new FastMessageBuilder();
        return Arrays.asList(builder, SafeMessageBuilder.wrap(builder));
    }

}
