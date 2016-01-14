package org.f1x.engine;

import org.f1x.FIXVersion;
import org.f1x.message.*;
import org.f1x.message.builder.MessageBuilder;
import org.f1x.message.fields.EncryptMethod;
import org.f1x.message.fields.FixTags;
import org.f1x.message.fields.MsgType;
import org.f1x.message.parser.MessageParser;
import org.f1x.state.SessionStatus;
import org.f1x.util.ByteSequence;

import static org.f1x.message.FieldUtil.checkPositive;
import static org.f1x.message.FieldUtil.checkPresent;

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
        ByteSequence testReqID = request.testReqID().clear();

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

    // TODO: optimize
    public static Header parseHeader(MessageParser parser, FIXVersion FIXVersion, Header header) {
      /*  parseBeginString(parser, fixVersion);
        parser.next();
        parseMessageType(parser, header.getMsgType());
        parseMsgSeqNumWithPossDup(parser, header);*/
        return header;
    }

    public static void parseBeginString(MessageParser parser) {
        int tag = parser.parseTag();
        if (tag != FixTags.BeginString)
            throw new FieldException(FixTags.BeginString, "Missing BeginString(8)");

        parser.parseValue();
    }

    public static CharSequence parseMessageType(MessageParser parser, ByteSequence out) {
        int tag = parser.parseTag();
        if (tag != FixTags.MsgType)
            throw new FieldException(FixTags.MsgType, "Missing MsgType(35)");

        parser.parseByteSequence(out);
        return out;
    }

    public static int parseBodyLength(MessageParser parser) {
        int tag = parser.parseTag();
        if (tag != FixTags.BodyLength)
            throw new FieldException(FixTags.BodyLength, "Missing BodyLength(9)");

        return checkPositive(FixTags.BodyLength, parser.parseInt());
    }

    // TODO: optimize
    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) throws InvalidFixMessageException {
       /* Boolean possDupFlag = null;
        int msgSeqNum = 0;
        while (parser.next()) {
            final int tagNum = parser.tag();
            if (tagNum == FixTags.MsgSeqNum) {
                msgSeqNum = parser.intValue();
                if (msgSeqNum < 1)
                    throw InvalidFixMessageException.MSG_SEQ_NUM_MUST_BE_POSITIVE;
                if (possDupFlag != null)
                    break; // we are done

            } else if (tagNum == FixTags.PossDupFlag) {
                possDupFlag = parser.booleanValue() ? Boolean.TRUE : Boolean.FALSE;
                if (msgSeqNum != 0)
                    break; // we are done
            }
        }

        if (msgSeqNum == 0)
            throw InvalidFixMessageException.NO_MSG_SEQ_NUM;

        header.msgSeqNum(msgSeqNum);
        header.possDup(possDupFlag == null ? false : possDupFlag);*/
    }
}
