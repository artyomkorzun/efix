package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class SettlSessID {

    public static final ByteSequence END_OF_DAY = of("EOD");
    public static final ByteSequence ELECTRONIC_TRADING_HOURS = of("ETH");
    public static final ByteSequence INTRADAY = of("ITD");
    public static final ByteSequence REGULAR_TRADING_HOURS = of("RTH");

}