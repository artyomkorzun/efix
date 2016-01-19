package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class StipulationValue {

    public static final ByteSequence SPECIAL_CUM_BONUS = of("CB");
    public static final ByteSequence SPECIAL_CUM_COUPON = of("CC");
    public static final ByteSequence SPECIAL_CUM_DIVIDEND = of("CD");
    public static final ByteSequence SPECIAL_CUM_CAPITAL_REPAYMENTS = of("CP");
    public static final ByteSequence SPECIAL_CUM_RIGHTS = of("CR");
    public static final ByteSequence CASH_SETTLEMENT = of("CS");
    public static final ByteSequence GUARANTEED_DELIVERY = of("GD");
    public static final ByteSequence SPECIAL_PRICE = of("SP");
    public static final ByteSequence REPORT_FOR_EUROPEAN_EQUITY_MARKET_SECURITIES = of("TR");
    public static final ByteSequence SPECIAL_EX_BONUS = of("XB");
    public static final ByteSequence SPECIAL_EX_COUPON = of("XC");
    public static final ByteSequence SPECIAL_EX_DIVIDEND = of("XD");
    public static final ByteSequence SPECIAL_EX_CAPITAL_REPAYMENTS = of("XP");
    public static final ByteSequence SPECIAL_EX_RIGHTS = of("XR");

}