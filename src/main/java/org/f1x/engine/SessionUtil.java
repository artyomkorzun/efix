package org.f1x.engine;

import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.field.EncryptMethod;
import org.f1x.message.field.MsgType;
import org.f1x.message.field.Tag;
import org.f1x.message.parser.MessageParser;
import org.f1x.state.SessionStatus;
import org.f1x.util.ByteSequence;
import org.f1x.util.ByteSequenceWrapper;

import static org.f1x.message.FieldUtil.*;


public class SessionUtil {

    public static void validateLogon(int heartBtInt, Logon logon) {
        int actual = checkPresent(Tag.HeartBtInt, logon.heartBtInt());
        if (actual != heartBtInt)
            throw new FieldException(Tag.HeartBtInt, String.format("HeartBtInt does not match, expected %s but received %s", heartBtInt, actual));
    }

    public static void validateTestRequest(TestRequest request) {
        checkPresent(Tag.TestReqID, request.testReqID());
    }

    public static void validateResendRequest(ResendRequest request) {
        int beginSeqNo = request.beginSeqNo();
        checkPresent(Tag.BeginSeqNo, beginSeqNo);
        checkPositive(Tag.BeginSeqNo, beginSeqNo);

        int endSeqNo = request.endSeqNo();
        checkPresent(Tag.EndSeqNo, endSeqNo);
        checkNonNegative(Tag.EndSeqNo, endSeqNo);

        if (endSeqNo != 0 && beginSeqNo > endSeqNo)
            throw new FieldException(Tag.EndSeqNo, String.format("BeginSeqNo(7) %s more EndSeqNo(16) %s", beginSeqNo, endSeqNo));
    }

    public static void validateSequenceReset(int targetSeqNum, SequenceReset reset) {
        int newSeqNo = checkPresent(Tag.BeginSeqNo, reset.newSeqNo());
        if (newSeqNo < targetSeqNum)
            throw new FieldException(Tag.NewSeqNo, String.format("NewSeqNo(36) %s less expected target MsgSeqNum %s", newSeqNo, targetSeqNum));
    }

    public static void makeLogon(boolean resetSeqNum, int heartBtInt, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.LOGON);
        builder.addInt(Tag.EncryptMethod, EncryptMethod.NONE_OTHER);
        builder.addInt(Tag.HeartBtInt, heartBtInt);
        builder.addBoolean(Tag.ResetSeqNumFlag, resetSeqNum);
    }

    public static void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.HEARTBEAT);
        if (testReqID != null)
            builder.addCharSequence(Tag.TestReqID, testReqID);
    }

    public static void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.TEST_REQUEST);
        builder.addCharSequence(Tag.TestReqID, testReqID);
    }

    public static void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.RESEND_REQUEST);
        builder.addInt(Tag.BeginSeqNo, beginSeqNo);
        builder.addInt(Tag.EndSeqNo, endSeqNo);
    }

    public static void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.SEQUENCE_RESET);
        builder.addBoolean(Tag.PossDupFlag, true);
        builder.addInt(Tag.NewSeqNo, newSeqNo);
        builder.addBoolean(Tag.GapFillFlag, gapFill);
    }

    public static void makeLogout(CharSequence text, MessageBuilder builder) {
        builder.addByteSequence(Tag.MsgType, MsgType.LOGOUT);
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

    public static void assertNotDuplicate(boolean possDup, String message) {
        if (possDup)
            throw new FieldException(Tag.PossDupFlag, message);
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

    public static Header parseHeader(MessageParser parser, Header header) {
        parseBeginString(parser);
        parseBodyLength(parser);
        parseMessageType(parser, header.msgType());
        parseMsgSeqNumWithPossDup(parser, header);
        return header;
    }

    public static void parseBeginString(MessageParser parser) {
        checkTag(Tag.BeginString, parser.parseTag());
        parser.parseValue();
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

    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) {
        int msgSeqNum = INT_NULL;
        boolean possDup = false;
        boolean possDupFound = false;

        while (parser.hasRemaining()) {
            int tagNum = parser.parseTag();
            if (tagNum == Tag.MsgSeqNum) {
                msgSeqNum = checkPositive(tagNum, parser.parseInt());
                if (possDupFound)
                    break;

            } else if (tagNum == Tag.PossDupFlag) {
                possDup = parser.parseBoolean();
                if (msgSeqNum != INT_NULL)
                    break;

                possDupFound = true;
            } else {
                if (!isHeader(tagNum))
                    break;

                parser.parseValue();
            }
        }

        checkPresent(Tag.MsgSeqNum, msgSeqNum);

        header.msgSeqNum(msgSeqNum);
        header.possDup(possDup);
    }

}
