package org.efix.message;

import org.efix.message.field.Tag;
import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;


public class FieldUtil {

    public static final int MIN_MESSAGE_LENGTH = 63;
    public static final byte TAG_VALUE_SEPARATOR = '=';
    public static final byte FIELD_SEPARATOR = '\u0001';
    public static final int CHECK_SUM_FIELD_LENGTH = 7;

    public static final long LONG_NULL = Long.MIN_VALUE;
    public static final long DECIMAL_NULL = Long.MIN_VALUE;
    public static final double DOUBLE_NULL = Double.NaN;
    public static final int INT_NULL = Integer.MIN_VALUE;
    public static final byte BYTE_NULL = Byte.MIN_VALUE;

    public static int checkTag(int expected, int tag) {
        if (tag != expected)
            throw new FieldException(tag, String.format("Expected field #%s but received %s", expected, tag));

        return tag;
    }

    public static long checkEqual(int tag, long value, long expected) {
        if (value != expected)
            throw new FieldException(tag, String.format("Field #%s does not match, expected %s but received %s", tag, expected, value));

        return value;
    }

    public static int checkEqual(int tag, int value, int expected) {
        if (value != expected)
            throw new FieldException(tag, String.format("Field #%s does not match, expected %s but received %s", tag, expected, value));

        return value;
    }

    public static byte checkEqual(int tag, byte value, byte expected) {
        if (value != expected)
            throw new FieldException(tag, String.format("Field #%s does not match, expected %s but received %s", tag, expected, value));

        return value;
    }

    public static ByteSequenceWrapper checkEqual(int tag, ByteSequenceWrapper value, ByteSequence expected){
        if(!value.equals(expected))
           throw new FieldException(tag, String.format("Field #%s does not match, expected %s but received %s", tag, expected, value));

        return value;
    }

    public static long checkPositive(int tag, long value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Field #%s with non positive value %s", tag, value));

        return value;
    }

    public static double checkPositive(int tag, double value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Field #%s with non positive value %s", tag, value));

        return value;
    }

    public static int checkPositive(int tag, int value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Field #%s with non positive value %s", tag, value));

        return value;
    }

    public static byte checkPositive(int tag, byte value) {
        if (value <= 0)
            throw new FieldException(tag, String.format("Field #%s with non positive value %s", tag, value));

        return value;
    }

    public static long checkNonNegative(int tag, long value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Field #%s with negative value %s", tag, value));

        return value;
    }

    public static double checkNonNegative(int tag, double value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Field #%s with negative value %s", tag, value));

        return value;
    }

    public static int checkNonNegative(int tag, int value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Field #%s with negative value %s", tag, value));

        return value;
    }

    public static byte checkNonNegative(int tag, byte value) {
        if (value < 0)
            throw new FieldException(tag, String.format("Field #%s with negative value %s", tag, value));

        return value;
    }

    public static long checkPresent(int tag, long value) {
        return checkPresent(tag, value, LONG_NULL);
    }

    public static double checkPresent(int tag, double value) {
        if (Double.isNaN(value))
            throw new FieldException(tag, String.format("Missing field #%s", tag));

        return value;
    }

    public static int checkPresent(int tag, int value) {
        return checkPresent(tag, value, INT_NULL);
    }

    public static byte checkPresent(int tag, byte value) {
        return checkPresent(tag, value, BYTE_NULL);
    }

    public static long checkPresent(int tag, long value, long nullValue) {
        if (value == nullValue)
            throw new FieldException(tag, String.format("Missing field #%s", tag));

        return value;
    }

    public static int checkPresent(int tag, int value, int nullValue) {
        if (value == nullValue)
            throw new FieldException(tag, String.format("Missing field #%s", tag));

        return value;
    }

    public static byte checkPresent(int tag, byte value, byte nullValue) {
        if (value == nullValue)
            throw new FieldException(tag, String.format("Missing field #%s", tag));

        return value;
    }

    public static <T extends ByteSequence> T checkPresent(int tag, T value) {
        if (value.length() == 0)
            throw new FieldException(tag, String.format("Missing field #%s", tag));

        return value;
    }

    public static int computeCheckSum(Buffer buffer, int offset, int length) {
        int checkSum = 0;
        for (int end = offset + length; offset < end; offset++)
            checkSum += buffer.getByte(offset);

        return checkSum & 0xFF;
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
