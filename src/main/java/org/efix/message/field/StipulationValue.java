package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class StipulationValue {

    public static final ByteSequence SPECIAL_CUM_BONUS = ByteSequenceWrapper.of("CB");
    public static final ByteSequence SPECIAL_CUM_COUPON = ByteSequenceWrapper.of("CC");
    public static final ByteSequence SPECIAL_CUM_DIVIDEND = ByteSequenceWrapper.of("CD");
    public static final ByteSequence SPECIAL_CUM_CAPITAL_REPAYMENTS = ByteSequenceWrapper.of("CP");
    public static final ByteSequence SPECIAL_CUM_RIGHTS = ByteSequenceWrapper.of("CR");
    public static final ByteSequence CASH_SETTLEMENT = ByteSequenceWrapper.of("CS");
    public static final ByteSequence GUARANTEED_DELIVERY = ByteSequenceWrapper.of("GD");
    public static final ByteSequence SPECIAL_PRICE = ByteSequenceWrapper.of("SP");
    public static final ByteSequence REPORT_FOR_EUROPEAN_EQUITY_MARKET_SECURITIES = ByteSequenceWrapper.of("TR");
    public static final ByteSequence SPECIAL_EX_BONUS = ByteSequenceWrapper.of("XB");
    public static final ByteSequence SPECIAL_EX_COUPON = ByteSequenceWrapper.of("XC");
    public static final ByteSequence SPECIAL_EX_DIVIDEND = ByteSequenceWrapper.of("XD");
    public static final ByteSequence SPECIAL_EX_CAPITAL_REPAYMENTS = ByteSequenceWrapper.of("XP");
    public static final ByteSequence SPECIAL_EX_RIGHTS = ByteSequenceWrapper.of("XR");

}