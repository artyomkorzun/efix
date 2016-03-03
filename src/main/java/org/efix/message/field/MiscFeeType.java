package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class MiscFeeType {

    public static final ByteSequence REGULATORY = ByteSequenceWrapper.of("1");
    public static final ByteSequence TAX = ByteSequenceWrapper.of("2");
    public static final ByteSequence LOCAL_COMMISSION = ByteSequenceWrapper.of("3");
    public static final ByteSequence EXCHANGE_FEES = ByteSequenceWrapper.of("4");
    public static final ByteSequence STAMP = ByteSequenceWrapper.of("5");
    public static final ByteSequence LEVY = ByteSequenceWrapper.of("6");
    public static final ByteSequence OTHER = ByteSequenceWrapper.of("7");
    public static final ByteSequence MARKUP = ByteSequenceWrapper.of("8");
    public static final ByteSequence CONSUMPTION_TAX = ByteSequenceWrapper.of("9");
    public static final ByteSequence PER_TRANSACTION = ByteSequenceWrapper.of("10");
    public static final ByteSequence CONVERSION = ByteSequenceWrapper.of("11");
    public static final ByteSequence AGENT = ByteSequenceWrapper.of("12");
    public static final ByteSequence TRANSFER_FEE = ByteSequenceWrapper.of("13");
    public static final ByteSequence SECURITY_LENDING = ByteSequenceWrapper.of("14");

}