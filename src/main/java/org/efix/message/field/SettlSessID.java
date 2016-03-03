package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class SettlSessID {

    public static final ByteSequence END_OF_DAY = ByteSequenceWrapper.of("EOD");
    public static final ByteSequence ELECTRONIC_TRADING_HOURS = ByteSequenceWrapper.of("ETH");
    public static final ByteSequence INTRADAY = ByteSequenceWrapper.of("ITD");
    public static final ByteSequence REGULAR_TRADING_HOURS = ByteSequenceWrapper.of("RTH");

}