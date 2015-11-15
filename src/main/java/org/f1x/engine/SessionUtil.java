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

import static org.f1x.message.Fields.checkPositive;
import static org.f1x.message.Fields.checkPresent;

public class SessionUtil {

    protected SessionUtil() {
    }

    public static void validateLogon(int heartBtInt, Logon logon) {
        int actual = Fields.checkPresent(FixTags.HeartBtInt, logon.heartBtInt(), -1);
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
        builder.add(FixTags.MsgType, MsgType.LOGON);
        builder.add(FixTags.EncryptMethod, EncryptMethod.NONE_OTHER);
        builder.add(FixTags.HeartBtInt, heartBtInt);
        builder.add(FixTags.ResetSeqNumFlag, resetSeqNum);
    }

    public static void makeHeartbeat(CharSequence testReqID, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.HEARTBEAT);
        if (testReqID != null)
            builder.add(FixTags.TestReqID, testReqID);
    }

    public static void makeTestRequest(CharSequence testReqID, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.TEST_REQUEST);
        builder.add(FixTags.TestReqID, testReqID);
    }

    public static void makeResendRequest(int beginSeqNo, int endSeqNo, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.RESEND_REQUEST);
        builder.add(FixTags.BeginSeqNo, beginSeqNo);
        builder.add(FixTags.EndSeqNo, endSeqNo);
    }

    public static void makeSequenceReset(boolean gapFill, int newSeqNo, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.SEQUENCE_RESET);
        builder.add(FixTags.PossDupFlag, true);
        builder.add(FixTags.NewSeqNo, newSeqNo);
        builder.add(FixTags.GapFillFlag, gapFill);
    }

    public static void makeLogout(CharSequence text, MessageBuilder builder) {
        builder.add(FixTags.MsgType, MsgType.LOGOUT);
        if (text != null)
            builder.add(FixTags.Text, text);
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

        while (parser.next()) {
            int tagNum = parser.tag();
            switch (tagNum) {
                case FixTags.HeartBtInt:
                    heartBtInt = parser.intValue();
                    break;
                case FixTags.ResetSeqNumFlag:
                    resetSeqNum = parser.booleanValue();
                    break;
            }
        }

        logon.heartBtInt(heartBtInt);
        logon.resetSeqNums(resetSeqNum);

        return logon;
    }

    public static TestRequest parseTestRequest(MessageParser parser, TestRequest request) {
        ByteSequence testReqID = request.testReqID().clear();

        while (parser.next()) {
            if (parser.tag() == FixTags.TestReqID) {
                parser.byteSequence(testReqID);
                break;
            }
        }

        return request;
    }

    public static ResendRequest parseResendRequest(MessageParser parser, ResendRequest request) {
        int beginSeqNo = -1;
        int endSeqNo = -1;
        while (parser.next()) {
            int tagNum = parser.tag();
            switch (tagNum) {
                case FixTags.BeginSeqNo:
                    beginSeqNo = parser.intValue();
                    break;
                case FixTags.EndSeqNo:
                    endSeqNo = parser.intValue();
                    break;
            }
        }

        request.beginSeqNo(beginSeqNo);
        request.endSeqNo(endSeqNo);

        return request;
    }

    public static SequenceReset parseSequenceReset(MessageParser parser, SequenceReset reset) {
        int newSeqNo = -1;
        boolean gapFillFlag = false;

        while (parser.next()) {
            int tagNum = parser.tag();
            switch (tagNum) {
                case FixTags.NewSeqNo:
                    newSeqNo = checkPositive(tagNum, parser.intValue());
                    break;
                case FixTags.GapFillFlag:
                    gapFillFlag = parser.booleanValue();
                    break;
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
        if (!parser.next() || parser.tag() != FixTags.BeginString)
            throw new FieldException(FixTags.BeginString, "Missing BeginString(8)");
    }

    public static CharSequence parseMessageType(MessageParser parser, ByteSequence out) {
        if (!parser.next() || parser.tag() != FixTags.MsgType)
            throw new FieldException(FixTags.MsgType, "Missing MsgType(35)");

        parser.byteSequence(out);
        return out;
    }

    public static int parseBodyLength(MessageParser parser) {
        if (!parser.next() || parser.tag() != FixTags.BodyLength)
            throw new FieldException(FixTags.BodyLength, "Missing BodyLength(9)");

        return checkPositive(FixTags.BodyLength, parser.intValue());
    }

    // TODO: optimize
    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) throws InvalidFixMessageException {
        Boolean possDupFlag = null;
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
        header.possDup(possDupFlag == null ? false : possDupFlag);
    }
}
