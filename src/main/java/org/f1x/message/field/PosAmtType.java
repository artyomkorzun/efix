package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class PosAmtType {

    public static final ByteSequence CASH_AMOUNT = of("CASH");
    public static final ByteSequence CASH_RESIDUAL_AMOUNT = of("CRES");
    public static final ByteSequence FINAL_MARK_TO_MARKET_AMOUNT = of("FMTM");
    public static final ByteSequence INCREMENTAL_MARK_TO_MARKET_AMOUNT = of("IMTM");
    public static final ByteSequence PREMIUM_AMOUNT = of("PREM");
    public static final ByteSequence SETTLEMENT_VALUE = of("SETL");
    public static final ByteSequence START_OF_DAY_MARK_TO_MARKET_AMOUNT = of("SMTM");
    public static final ByteSequence TRADE_VARIATION_AMOUNT = of("TVAR");
    public static final ByteSequence VALUE_ADJUSTED_AMOUNT = of("VADJ");

}