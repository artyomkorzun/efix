package org.f1x.engine;

import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.EncryptMethod;
import org.f1x.message.fields.FixTags;
import org.f1x.message.fields.MsgType;
import org.f1x.message.parser.MessageParser;
import org.f1x.state.SessionStatus;
import org.f1x.util.ByteSequenceWrapper;

import static org.f1x.message.FieldUtil.*;

public class SessionUtil {

    public static void validateLogon(int heartBtInt, Logon logon) {
        int actual = FieldUtil.checkPresent(FixTags.HeartBtInt, logon.heartBtInt(), -1);
        if (actual != heartBtInt)
            throw new FieldException(FixTags.HeartBtInt, String.format("HeartBtInt does not match, expected %s but received %s", heartBtInt, actual));
    }

    public static void validateTestRequest(TestRequest request) {
        checkPresent(FixTags.TestReqID, request.testReqID());
    }

    public static void validateResendRequest(ResendRequest request) {
        int beginSeqNo = request.beginSeqNo();
        checkPresent(FixTags.BeginSeqNo, beginSeqNo, -1);
        checkPositive(FixTags.BeginSeqNo, beginSeqNo);

        int endSeqNo = request.endSeqNo();
        checkPresent(FixTags.EndSeqNo, endSeqNo, -1);
        checkPositive(FixTags.EndSeqNo, endSeqNo);

        if (endSeqNo != 0 && beginSeqNo > endSeqNo)
            throw new FieldException(FixTags.EndSeqNo, String.format("BeginSeqNo(7) %s is more EndSeqNo(16) %s", beginSeqNo, endSeqNo));
    }

    public static void validateSequenceReset(int targetSeqNum, SequenceReset reset) {
        int newSeqNo = reset.newSeqNo();
        if (newSeqNo < targetSeqNum)
            throw new FieldException(FixTags.NewSeqNo, String.format("NewSeqNo(36) %s less expected MsgSeqNum %s", newSeqNo, targetSeqNum));
    }

    public static void makeLogon(boolean resetSeqNum, int heartBtInt, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.LOGON.getBytes());
        builder.addByte(FixTags.EncryptMethod, EncryptMethod.NONE_OTHER.getCode());
        builder.addInt(FixTags.HeartBtInt, heartBtInt);
        builder.addBoolean(FixTags.ResetSeqNumFlag, resetSeqNum);
    }

    public static void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.HEARTBEAT.getBytes());
        if (testReqID != null)
            builder.addCharSequence(FixTags.TestReqID, testReqID);
    }

    public static void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.TEST_REQUEST.getBytes());
        builder.addCharSequence(FixTags.TestReqID, testReqID);
    }

    public static void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.RESEND_REQUEST.getBytes());
        builder.addInt(FixTags.BeginSeqNo, beginSeqNo);
        builder.addInt(FixTags.EndSeqNo, endSeqNo);
    }

    public static void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.SEQUENCE_RESET.getBytes());
        builder.addBoolean(FixTags.PossDupFlag, true);
        builder.addInt(FixTags.NewSeqNo, newSeqNo);
        builder.addBoolean(FixTags.GapFillFlag, gapFill);
    }

    public static void makeLogout(CharSequence text, MessageBuilder builder) {
        builder.addBytes(FixTags.MsgType, MsgType.LOGOUT.getBytes());
        if (text != null)
            builder.addCharSequence(FixTags.Text, text);
    }

    public static boolean checkTargetSeqNum(int expected, int actual, boolean checkHigher) {
        if (actual < expected)
            throw new FieldException(FixTags.MsgSeqNum, String.format("MsgSeqNum too low, expecting %s but received %s", expected, actual));

        if (checkHigher && actual > expected)
            throw new FieldException(FixTags.MsgSeqNum, String.format("MsgSeqNum too high, expecting %s but received %s", expected, actual));

        return actual == expected;
    }

    public static void assertNotDuplicate(boolean possDup, String message) {
        if (possDup)
            throw new FieldException(FixTags.PossDupFlag, message);
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
                case FixTags.HeartBtInt:
                    heartBtInt = parser.parseInt();
                    break;
                case FixTags.ResetSeqNumFlag:
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
            if (tag == FixTags.TestReqID)
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
                case FixTags.BeginSeqNo:
                    beginSeqNo = parser.parseInt();
                    break;
                case FixTags.EndSeqNo:
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
                case FixTags.NewSeqNo:
                    newSeqNo = checkPositive(tag, parser.parseInt());
                    break;
                case FixTags.GapFillFlag:
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
        checkTag(parser.parseTag(), FixTags.BeginString);
        parser.parseValue();
    }

    public static CharSequence parseMessageType(MessageParser parser, ByteSequenceWrapper out) {
        checkTag(parser.parseTag(), FixTags.MsgType);
        parser.parseByteSequence(out);
        return out;
    }

    public static int parseBodyLength(MessageParser parser) {
        checkTag(parser.parseTag(), FixTags.BodyLength);
        return checkPositive(FixTags.BodyLength, parser.parseInt());
    }

    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) {
        int msgSeqNum = -1;
        boolean possDup = false;
        boolean possDupFound = false;

        while (parser.hasRemaining()) {
            int tagNum = parser.parseTag();
            if (tagNum == FixTags.MsgSeqNum) {
                msgSeqNum = checkPositive(tagNum, parser.parseInt());
                if (possDupFound)
                    break;
            } else if (tagNum == FixTags.PossDupFlag) {
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

        checkPresent(FixTags.MsgSeqNum, msgSeqNum, -1);

        header.msgSeqNum(msgSeqNum);
        header.possDup(possDup);
    }

    public static boolean isHeaderField(int tag) {
        switch (tag) {
            case FixTags.BeginString:
            case FixTags.BodyLength:
            case FixTags.MsgType:
            case FixTags.SenderCompID:
            case FixTags.SenderSubID:
            case FixTags.TargetCompID:
            case FixTags.TargetSubID:
            case FixTags.OnBehalfOfCompID:
            case FixTags.DeliverToCompID:
            case FixTags.SecureData:
            case FixTags.MsgSeqNum:
            case FixTags.SenderLocationID:
            case FixTags.TargetLocationID:
            case FixTags.OnBehalfOfSubID:
            case FixTags.OnBehalfOfLocationID:
            case FixTags.DeliverToSubID:
            case FixTags.DeliverToLocationID:
            case FixTags.PossDupFlag:
            case FixTags.PossResend:
            case FixTags.SendingTime:
            case FixTags.OrigSendingTime:
            case FixTags.XmlDataLen:
            case FixTags.XmlData:
            case FixTags.MessageEncoding:
            case FixTags.LastMsgSeqNumProcessed:
            case FixTags.NoHops:
            case FixTags.HopCompID:
            case FixTags.HopSendingTime:
            case FixTags.HopRefID:
            case 1128:
            case 1156:
            case 1129:
                return true;
            default:
                return false;
        }
    }

}
