package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.message.field.MsgType;
import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;

import static org.efix.util.TestUtil.parseTimestamp;
import static org.efix.util.TestUtil.stringMessage;
import static org.efix.util.buffer.BufferUtil.fromString;


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

    @Test(expected = InsufficientSpaceException.class)
    public void shouldThrowExceptionMessageLengthExceedsMaxOnPackSendMessage() {
        SessionId sessionId = new SessionId("S", "T");
        FixVersion fixVersion = FixVersion.FIX42;

        int messageLength = 67;
        UnsafeBuffer buffer = UnsafeBuffer.allocateHeap(messageLength - 1);
        MessagePacker packer = new MessagePacker(buffer);

        int seqNum = 1;
        long time = System.currentTimeMillis();
        ByteSequence msgType = MsgType.ORDER_SINGLE;
        Buffer body = UnsafeBuffer.allocateHeap(0);

        packer.pack(fixVersion, sessionId, seqNum, time, msgType, body, 0, 0);
    }

    @Test(expected = InsufficientSpaceException.class)
    public void shouldThrowExceptionMessageLengthExceedsMaxOnPackResendMessage() {
        SessionId sessionId = new SessionId("S", "T");
        FixVersion fixVersion = FixVersion.FIX42;

        int messageLength = 109;
        UnsafeBuffer buffer = UnsafeBuffer.allocateHeap(messageLength - 1);
        MessagePacker packer = new MessagePacker(buffer);

        int seqNum = 99;
        long time = System.currentTimeMillis();
        ByteSequence msgType = MsgType.EXECUTION_REPORT;
        Buffer body = UnsafeBuffer.allocateHeap(10);

        packer.pack(fixVersion, sessionId, seqNum, time, time, msgType, body, 0, 10);
    }

    protected static void shouldPackSendMessage(String expected, String senderCompID, String senderSubId, String targetCompID, String targetSubId,
                                                int msgSeqNum, String sendingTime, String msgType, String body) {

        SessionId id = new SessionId(senderCompID, senderSubId, targetCompID, targetSubId);
        FixVersion fixVersion = FixVersion.FIX44;

        body = stringMessage(body);

        UnsafeBuffer buffer = UnsafeBuffer.allocateHeap(1024);
        MessagePacker packer = new MessagePacker(buffer);
        int length = packer.pack(fixVersion, id, msgSeqNum, parseTimestamp(sendingTime), ByteSequenceWrapper.of(msgType), fromString(body), 0, body.length());

        String actual = BufferUtil.toString(buffer, 0, length);
        expected = stringMessage(expected);
        Assert.assertEquals("should pack message " + expected, expected, actual);
    }

    protected static void shouldPackResendMessage(String expected, String senderCompID, String senderSubId, String targetCompID, String targetSubId,
                                                  int msgSeqNum, String sendingTime, String origSendingTime, String msgType, String body) {

        SessionId id = new SessionId(senderCompID, senderSubId, targetCompID, targetSubId);
        FixVersion fixVersion = FixVersion.FIX44;

        body = stringMessage(body);

        UnsafeBuffer buffer = UnsafeBuffer.allocateHeap(1024);
        MessagePacker packer = new MessagePacker(buffer);
        int length = packer.pack(fixVersion, id, msgSeqNum, parseTimestamp(sendingTime), parseTimestamp(origSendingTime), ByteSequenceWrapper.of(msgType), fromString(body), 0, body.length());

        String actual = BufferUtil.toString(buffer, 0, length);
        expected = stringMessage(expected);
        Assert.assertEquals("should pack message " + expected, expected, actual);
    }

}
