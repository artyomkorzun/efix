package org.f1x.message;


import org.f1x.message.field.Tag;
import org.f1x.util.ByteSequence;

public class FieldUtil {

    public static final int MIN_MESSAGE_LENGTH = 63;
    public static final byte TAG_VALUE_SEPARATOR = '=';
    public static final byte FIELD_SEPARATOR = '\u0001';
    public static final int CHECK_SUM_FIELD_LENGTH = 7;

    public static final int INT_NULL = Integer.MIN_VALUE;

    public static int checkSum(int sum) {
        return sum & 0xFF;
    }

    public static int checkTag(int expected, int tag) {
        if (tag != expected)
            throw new FieldException(tag, String.format("Unexpected field %s, expected %s", tag, expected));

        return tag;
    }

    public static int checkEqual(int tag, int value, int expected) {
        if (value != expected)
            throw new FieldException(tag, String.format("Field %s with value %s not equal %s", tag, value, expected));

        return value;
    }

    public static int checkPositive(int tag, int value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Field %s with non positive value %s", tag, value));

        return value;
    }

    public static int checkNonNegative(int tag, int value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Field %s with negative value %s", tag, value));

        return value;
    }

    public static int checkPresent(int tag, int value) {
        return checkPresent(tag, value, INT_NULL);
    }

    public static int checkPresent(int tag, int value, int nullValue) {
        if (value == nullValue)
            throw new FieldException(tag, String.format("Missing field %s", tag));

        return value;
    }

    public static <T extends ByteSequence> T checkPresent(int tag, T value) {
        if (value.length() == 0)
            throw new FieldException(tag, String.format("Missing field %s", tag));

        return value;
    }

    public static boolean isHeader(int tag) {
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
