package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class MiscFeeType {

    public static final ByteSequence REGULATORY = of("1");
    public static final ByteSequence TAX = of("2");
    public static final ByteSequence LOCAL_COMMISSION = of("3");
    public static final ByteSequence EXCHANGE_FEES = of("4");
    public static final ByteSequence STAMP = of("5");
    public static final ByteSequence LEVY = of("6");
    public static final ByteSequence OTHER = of("7");
    public static final ByteSequence MARKUP = of("8");
    public static final ByteSequence CONSUMPTION_TAX = of("9");
    public static final ByteSequence PER_TRANSACTION = of("10");
    public static final ByteSequence CONVERSION = of("11");
    public static final ByteSequence AGENT = of("12");
    public static final ByteSequence TRANSFER_FEE = of("13");
    public static final ByteSequence SECURITY_LENDING = of("14");

}