package org.f1x.message.parser;

import org.f1x.FIXVersion;
import org.f1x.message.*;
import org.f1x.message.fields.FixTags;
import org.f1x.util.ByteArrayReference;

import static org.f1x.util.Checker.checkPositive;

public class MessageParsers {

    public static Logon parseLogon(MessageParser parser, Logon logon) {
        int heartBtInt = -1;
        boolean resetSeqNum = false;

        while (parser.next()) {
            int tagNum = parser.getTagNum();
            switch (tagNum) {
                case FixTags.HeartBtInt:
                    heartBtInt = parser.getIntValue();
                    break;
                case FixTags.ResetSeqNumFlag:
                    resetSeqNum = parser.getBooleanValue();
                    break;
            }
        }

        logon.heartBtInt(heartBtInt);
        logon.resetSeqNums(resetSeqNum);

        return logon;
    }

    public static TestRequest parseTestRequest(MessageParser parser, TestRequest request) {
        ByteArrayReference testReqID = request.testReqID().clear();

        while (parser.next()) {
            if (parser.getTagNum() == FixTags.TestReqID) {
                parser.getByteSequence(testReqID);
                break;
            }
        }

        return request;
    }

    public static ResendRequest parseResendRequest(MessageParser parser, ResendRequest request) {
        int beginSeqNo = -1;
        int endSeqNo = -1;
        while (parser.next()) {
            int tagNum = parser.getTagNum();
            switch (tagNum) {
                case FixTags.BeginSeqNo:
                    beginSeqNo = parser.getIntValue();
                    break;
                case FixTags.EndSeqNo:
                    endSeqNo = parser.getIntValue();
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
            int tagNum = parser.getTagNum();
            switch (tagNum) {
                case FixTags.NewSeqNo:
                    newSeqNo = checkPositive(tagNum, parser.getIntValue());
                    break;
                case FixTags.GapFillFlag:
                    gapFillFlag = parser.getBooleanValue();
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
        if (!parser.next() || parser.getTagNum() != FixTags.BeginString)
            throw new FieldException(FixTags.BeginString, "Missing BeginString(8)");
    }

    public static void parseMessageType(MessageParser parser, ByteArrayReference out) {
        if (!parser.next() || parser.getTagNum() != FixTags.MsgType)
            throw new FieldException(FixTags.MsgType, "Missing MsgType(35)");

        parser.getByteSequence(out);
    }

    public static int parseBodyLength(MessageParser parser) {
        if (!parser.next() || parser.getTagNum() != FixTags.BodyLength)
            throw new FieldException(FixTags.BodyLength, "Missing BodyLength(9)");

        return checkPositive(FixTags.BodyLength, parser.getIntValue());
    }


    // TODO: optimize
    public static void parseMsgSeqNumWithPossDup(MessageParser parser, Header header) throws InvalidFixMessageException {
        Boolean possDupFlag = null;
        int msgSeqNum = 0;
        while (parser.next()) {
            final int tagNum = parser.getTagNum();
            if (tagNum == FixTags.MsgSeqNum) {
                msgSeqNum = parser.getIntValue();
                if (msgSeqNum < 1)
                    throw InvalidFixMessageException.MSG_SEQ_NUM_MUST_BE_POSITIVE;
                if (possDupFlag != null)
                    break; // we are done

            } else if (tagNum == FixTags.PossDupFlag) {
                possDupFlag = parser.getBooleanValue() ? Boolean.TRUE : Boolean.FALSE;
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
