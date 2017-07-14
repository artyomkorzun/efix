package org.efix.session;

import org.efix.message.*;
import org.efix.message.builder.MessageBuilder;
import org.efix.message.field.EncryptMethod;
import org.efix.message.field.Tag;
import org.efix.message.parser.MessageParser;
import org.efix.state.SessionStatus;
import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;

import static org.efix.message.FieldUtil.*;


public class SessionUtil {

    public static void makeLogon(boolean resetSeqNum, int heartBtInt, MessageBuilder builder) {
        builder.addInt(Tag.EncryptMethod, EncryptMethod.NONE_OTHER);
        builder.addInt(Tag.HeartBtInt, heartBtInt);
        builder.addBoolean(Tag.ResetSeqNumFlag, resetSeqNum);
    }

    public static void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        if (testReqID != null)
            builder.addCharSequence(Tag.TestReqID, testReqID);
    }

    public static void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        builder.addCharSequence(Tag.TestReqID, testReqID);
    }

    public static void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        builder.addInt(Tag.BeginSeqNo, beginSeqNo);
        builder.addInt(Tag.EndSeqNo, endSeqNo);
    }

    public static void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        builder.addBoolean(Tag.PossDupFlag, true);
        builder.addInt(Tag.NewSeqNo, newSeqNo);
        builder.addBoolean(Tag.GapFillFlag, gapFill);
    }

    public static void makeLogout(CharSequence text, MessageBuilder builder) {
        if (text != null)
            builder.addCharSequence(Tag.Text, text);
    }

    public static boolean checkTargetSeqNum(int expected, int actual, boolean checkHigher) {
        if (actual < expected)
            throw new FieldException(Tag.MsgSeqNum, String.format("MsgSeqNum too low, expecting %s but received %s", expected, actual));

        if (checkHigher && actual > expected)
            throw new FieldException(Tag.MsgSeqNum, String.format("MsgSeqNum too high, expecting %s but received %s", expected, actual));

        return actual == expected;
    }

    public static void assertStatus(SessionStatus expected1, SessionStatus expected2, SessionStatus expected3, SessionStatus actual) {
        if (actual != expected1 && actual != expected2 && actual != expected3)
            throw new IllegalStateException(String.format("Expected statuses %s, %s, %s but actual %s", expected1, expected2, expected3, actual));
    }

    public static void assertStatus(SessionStatus expected1, SessionStatus expected2, SessionStatus actual) {
        if (actual != expected1 && actual != expected2)
            throw new IllegalStateException(String.format("Expected statuses %s and %s but actual %s", expected1, expected2, actual));
    }

    public static Logon parseLogon(MessageParser parser, Logon logon) {
        int heartBtInt = INT_NULL;
        boolean resetSeqNum = false;

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.HeartBtInt:
                    heartBtInt = parser.parseInt();
                    break;
                case Tag.ResetSeqNumFlag:
                    resetSeqNum = parser.parseBoolean();
                    break;
                default:
                    parser.parseValue();
            }
        }

        logon.heartBtInt(heartBtInt);
        logon.resetSeqNums(resetSeqNum);

        return logon;
    }

    public static TestRequest parseTestRequest(MessageParser parser, TestRequest request) {
        ByteSequenceWrapper testReqID = request.testReqID().clear();

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            if (tag == Tag.TestReqID)
                parser.parseByteSequence(testReqID);
            else
                parser.parseValue();
        }

        return request;
    }

    public static ResendRequest parseResendRequest(MessageParser parser, ResendRequest request) {
        int beginSeqNo = INT_NULL;
        int endSeqNo = INT_NULL;
        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.BeginSeqNo:
                    beginSeqNo = parser.parseInt();
                    break;
                case Tag.EndSeqNo:
                    endSeqNo = parser.parseInt();
                    break;
                default:
                    parser.parseValue();
            }
        }

        request.beginSeqNo(beginSeqNo);
        request.endSeqNo(endSeqNo);

        return request;
    }

    public static SequenceReset parseSequenceReset(MessageParser parser, SequenceReset reset) {
        int newSeqNo = INT_NULL;
        boolean gapFill = false;

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.NewSeqNo:
                    newSeqNo = parser.parseInt();
                    break;
                case Tag.GapFillFlag:
                    gapFill = parser.parseBoolean();
                    break;
                default:
                    parser.parseValue();
            }
        }

        reset.newSeqNo(newSeqNo);
        reset.gapFill(gapFill);

        return reset;
    }

    public static void parseHeader(Message message, Header header) {
        message.getString(Tag.MsgType, header.msgType());
        int msgSeqNum = message.getUInt(Tag.MsgSeqNum);
        boolean possDup = message.getBool(Tag.PossDupFlag, false);

        header.msgSeqNum(msgSeqNum);
        header.possDup(possDup);
    }

    public static void parseBeginString(MessageParser parser) {
        checkTag(Tag.BeginString, parser.parseTag());
        parser.parseValue();
    }

    public static void parseBeginString(MessageParser parser, ByteSequenceWrapper out) {
        checkTag(Tag.BeginString, parser.parseTag());
        parser.parseByteSequence(out);
    }

    public static int parseBodyLength(MessageParser parser) {
        checkTag(Tag.BodyLength, parser.parseTag());
        return checkPositive(Tag.BodyLength, parser.parseInt());
    }

    public static ByteSequence parseMessageType(MessageParser parser, ByteSequenceWrapper out) {
        checkTag(Tag.MsgType, parser.parseTag());
        parser.parseByteSequence(out);
        return out;
    }

    public static int parseCheckSum(MessageParser parser) {
        checkTag(Tag.CheckSum, parser.parseTag());
        return parser.parseInt();
    }

}
