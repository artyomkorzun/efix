package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.SessionIDBean;
import org.f1x.message.builder.FastMessageBuilder;
import org.f1x.util.BufferUtil;
import org.f1x.util.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import static org.f1x.util.BufferUtil.fromString;
import static org.f1x.util.TestUtil.normalize;
import static org.f1x.util.TestUtil.parseTimestamp;

public class MessagePackerTest {

    @Test
    public void shouldPackSendMessages() {
        String expected = "8=FIX.4.4|9=95|35=D|34=10|49=senderComp|50=senderSub|56=targetComp|57=targetSub|52=20000101-00:00:00.000|1=11|10=146|";
        shouldPackSendMessage(expected, "senderComp", "senderSub", "targetComp", "targetSub", 10, "20000101-00:00:00", "D", "1=11|");

        expected = "8=FIX.4.4|9=70|35=D|34=10|49=senderComp|56=targetComp|52=20000101-00:00:00.000|55=ES|10=081|";
        shouldPackSendMessage(expected, "senderComp", null, "targetComp", null, 10, "20000101-00:00:00", "D", "55=ES|");

        expected = "8=FIX.4.4|9=69|35=D|34=10|49=senderComp|56=targetComp|52=20000101-00:00:00.000|55=A|10=002|";
        shouldPackSendMessage(expected, "senderComp", null, "targetComp", null, 10, "20000101-00:00:00", "D", "55=A|");
    }

    @Test
    public void shouldPackResendMessages() {
        String expected = "8=FIX.4.4|9=126|35=D|34=10|49=senderComp|50=senderSub|56=targetComp|57=targetSub|52=20000101-00:00:00.000|122=19700101-00:00:00.000|43=Y|1=11|10=160|";
        shouldPackResendMessage(expected, "senderComp", "senderSub", "targetComp", "targetSub", 10, "20000101-00:00:00", "19700101-00:00:00", "D", "1=11|");

        expected = "8=FIX.4.4|9=101|35=D|34=10|49=senderComp|56=targetComp|52=20000101-00:00:00.000|122=19700101-00:00:00.000|43=Y|55=ES|10=095|";
        shouldPackResendMessage(expected, "senderComp", null, "targetComp", null, 10, "20000101-00:00:00", "19700101-00:00:00", "D", "55=ES|");

        expected = "8=FIX.4.4|9=100|35=D|34=10|49=senderComp|56=targetComp|52=20000101-00:00:00.000|122=19700101-00:00:00.000|43=Y|55=A|10=007|";
        shouldPackResendMessage(expected, "senderComp", null, "targetComp", null, 10, "20000101-00:00:00", "19700101-00:00:00", "D", "55=A|");
    }

    protected static void shouldPackSendMessage(String expected, String senderCompID, String senderSubId, String targetCompID, String targetSubId,
                                                int msgSeqNum, String sendingTime, String msgType, String body) {

        SessionIDBean id = new SessionIDBean(senderCompID, senderSubId, targetCompID, targetSubId);
        body = normalize(body);

        MessagePacker packer = new MessagePacker(FIXVersion.FIX44, id, new FastMessageBuilder(), BufferUtil.allocate(1024));
        Buffer buffer = packer.pack(msgSeqNum, parseTimestamp(sendingTime), msgType, fromString(body), 0, body.length());

        String actual = BufferUtil.toString(buffer);
        expected = normalize(expected);
        Assert.assertEquals("should pack message " + expected, expected, actual);
    }

    protected static void shouldPackResendMessage(String expected, String senderCompID, String senderSubId, String targetCompID, String targetSubId,
                                                  int msgSeqNum, String sendingTime, String origSendingTime, String msgType, String body) {

        SessionIDBean id = new SessionIDBean(senderCompID, senderSubId, targetCompID, targetSubId);
        body = normalize(body);

        MessagePacker packer = new MessagePacker(FIXVersion.FIX44, id, new FastMessageBuilder(), BufferUtil.allocate(1024));
        Buffer buffer = packer.pack(msgSeqNum, parseTimestamp(sendingTime), parseTimestamp(origSendingTime), msgType, fromString(body), 0, body.length());

        String actual = BufferUtil.toString(buffer);
        expected = normalize(expected);
        Assert.assertEquals("should pack message " + expected, expected, actual);
    }

}
