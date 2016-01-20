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
        int actual = checkPresent(Tag.HeartBtInt, logon.heartBtInt(), -1);
        if (actual != heartBtInt)
            throw new FieldException(Tag.HeartBtInt, String.format("HeartBtInt does not match, expected %s but received %s", heartBtInt, actual));
    }

    public static void validateTestRequest(TestRequest request) {
        checkPresent(Tag.TestReqID, request.testReqID());
    }

    public static void validateResendRequest(ResendRequest request) {
        int beginSeqNo = request.beginSeqNo();
        checkPresent(Tag.BeginSeqNo, beginSeqNo, -1);
        checkPositive(Tag.BeginSeqNo, beginSeqNo);

        int endSeqNo = request.endSeqNo();
        checkPresent(Tag.EndSeqNo, endSeqNo, -1);
        checkPositive(Tag.EndSeqNo, endSeqNo);

        if (endSeqNo != 0 && beginSeqNo > endSeqNo)
            throw new FieldException(Tag.EndSeqNo, String.format("BeginSeqNo(7) %s is more EndSeqNo(16) %s", beginSeqNo, endSeqNo));
    }

    public static void validateSequenceReset(int targetSeqNum, SequenceReset reset) {
        int newSeqNo = reset.newSeqNo();
        if (newSeqNo < targetSeqNum)
            throw new FieldException(Tag.NewSeqNo, String.format("NewSeqNo(36) %s less expected MsgSeqNum %s", newSeqNo, targetSeqNum));
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
        int heartBtInt = -1;
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
        int beginSeqNo = -1;
        int endSeqNo = -1;
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
        int newSeqNo = -1;
        boolean gapFillFlag = false;

        while (parser.hasRemaining()) {
            int tag = parser.parseTag();
            switch (tag) {
                case Tag.NewSeqNo:
                    newSeqNo = checkPositive(tag, parser.parseInt());
                    break;
                case Tag.GapFillFlag:
                    gapFillFlag = parser.parseBoolean();
                    break;
                default:
                    parser.parseValue();
            }
        }

        reset.newSeqNo(newSeqNo);
        reset.gapFill(gapFillFlag);

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
        checkTag(parser.parseTag(), Tag.BeginString);
        parser.parseValue();
    }

    public static ByteSequence parseMessageType(MessageParser parser, ByteSequenceWrapper out) {
        checkTag(parser.parseTag(), Tag.MsgType);
        parser.parseByteSequence(out);
        return out;
    }

    public static int parseBodyLength(MessageParser parser) {
        checkTag(parser.parseTag(), Tag.BodyLength);
        return checkPositive(Tag.BodyLength, parser.parseInt());
    }

    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) {
        int msgSeqNum = -1;
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
                if (msgSeqNum > 0)
                    break;

                possDupFound = true;
            } else {
                if (!isHeaderField(tagNum))
                    break;

                parser.parseValue();
            }
        }

        checkPresent(Tag.MsgSeqNum, msgSeqNum, -1);

        header.msgSeqNum(msgSeqNum);
        header.possDup(possDup);
    }

    public static boolean isHeaderField(int tag) {
        switch (tag) {
            case Tag.BeginString:
            case Tag.BodyLength:
            case Tag.MsgType:
            case Tag.SenderCompID:
            case Tag.SenderSubID:
            case Tag.TargetCompID:
            case Tag.TargetSubID:
            case Tag.OnBehalfOfCompID:
            case Tag.DeliverToCompID:
            case Tag.SecureData:
            case Tag.MsgSeqNum:
            case Tag.SenderLocationID:
            case Tag.TargetLocationID:
            case Tag.OnBehalfOfSubID:
            case Tag.OnBehalfOfLocationID:
            case Tag.DeliverToSubID:
            case Tag.DeliverToLocationID:
            case Tag.PossDupFlag:
            case Tag.PossResend:
            case Tag.SendingTime:
            case Tag.OrigSendingTime:
            case Tag.XmlDataLen:
            case Tag.XmlData:
            case Tag.MessageEncoding:
            case Tag.LastMsgSeqNumProcessed:
            case Tag.NoHops:
            case Tag.HopCompID:
            case Tag.HopSendingTime:
            case Tag.HopRefID:
            case Tag.ApplVerID:
            case Tag.CstmApplVerID:
            case Tag.ApplExtID:
                return true;
            default:
                return false;
        }
    }

}
