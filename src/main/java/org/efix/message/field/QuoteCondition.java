package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class QuoteCondition {

    public static final ByteSequence RESERVED_SAM = ByteSequenceWrapper.of("0");
    public static final ByteSequence NO_ACTIVE_SAM = ByteSequenceWrapper.of("1");
    public static final ByteSequence RESTRICTED = ByteSequenceWrapper.of("2");
    public static final ByteSequence OPEN_ACTIVE = ByteSequenceWrapper.of("A");
    public static final ByteSequence CLOSED_INACTIVE = ByteSequenceWrapper.of("B");
    public static final ByteSequence EXCHANGE_BEST = ByteSequenceWrapper.of("C");
    public static final ByteSequence CONSOLIDATED_BEST = ByteSequenceWrapper.of("D");
    public static final ByteSequence LOCKED = ByteSequenceWrapper.of("E");
    public static final ByteSequence CROSSED = ByteSequenceWrapper.of("F");
    public static final ByteSequence DEPTH = ByteSequenceWrapper.of("G");
    public static final ByteSequence FAST_TRADING = ByteSequenceWrapper.of("H");
    public static final ByteSequence NON_FIRM = ByteSequenceWrapper.of("I");
    public static final ByteSequence OUTRIGHT_PRICE = ByteSequenceWrapper.of("J");
    public static final ByteSequence IMPLIED_PRICE = ByteSequenceWrapper.of("K");
    public static final ByteSequence MANUAL_SLOW_QUOTE = ByteSequenceWrapper.of("L");
    public static final ByteSequence DEPTH_ON_OFFER = ByteSequenceWrapper.of("M");
    public static final ByteSequence DEPTH_ON_BID = ByteSequenceWrapper.of("N");
    public static final ByteSequence CLOSING = ByteSequenceWrapper.of("O");
    public static final ByteSequence NEWS_DISSEMINATION = ByteSequenceWrapper.of("P");
    public static final ByteSequence TRADING_RANGE = ByteSequenceWrapper.of("Q");
    public static final ByteSequence ORDER_INFLUX = ByteSequenceWrapper.of("R");
    public static final ByteSequence DUE_TO_RELATED = ByteSequenceWrapper.of("S");
    public static final ByteSequence NEWS_PENDING = ByteSequenceWrapper.of("T");
    public static final ByteSequence ADDITIONAL_INFO = ByteSequenceWrapper.of("U");
    public static final ByteSequence ADDITIONAL_INFO_DUE_TO_RELATED = ByteSequenceWrapper.of("V");
    public static final ByteSequence RESUME = ByteSequenceWrapper.of("W");
    public static final ByteSequence VIEW_OF_COMMON = ByteSequenceWrapper.of("X");
    public static final ByteSequence VOLUME_ALERT = ByteSequenceWrapper.of("Y");
    public static final ByteSequence ORDER_IMBALANCE = ByteSequenceWrapper.of("Z");
    public static final ByteSequence EQUIPMENT_CHANGEOVER = ByteSequenceWrapper.of("a");
    public static final ByteSequence NO_OPEN = ByteSequenceWrapper.of("b");
    public static final ByteSequence REGULAR_ETH = ByteSequenceWrapper.of("c");
    public static final ByteSequence AUTOMATIC_EXECUTION = ByteSequenceWrapper.of("d");
    public static final ByteSequence AUTOMATIC_EXECUTION_ETH = ByteSequenceWrapper.of("e");
    public static final ByteSequence FAST_MARKET_ETH = ByteSequenceWrapper.of("f");
    public static final ByteSequence INACTIVE_ETH = ByteSequenceWrapper.of("g");
    public static final ByteSequence ROTATION = ByteSequenceWrapper.of("h");
    public static final ByteSequence ROTATION_ETH = ByteSequenceWrapper.of("i");
    public static final ByteSequence HALT = ByteSequenceWrapper.of("j");
    public static final ByteSequence HALT_ETH = ByteSequenceWrapper.of("k");
    public static final ByteSequence DUE_TO_NEWS_DISSEMINATION = ByteSequenceWrapper.of("l");
    public static final ByteSequence DUE_TO_NEWS_PENDING = ByteSequenceWrapper.of("m");
    public static final ByteSequence TRADING_RESUME = ByteSequenceWrapper.of("n");
    public static final ByteSequence OUT_OF_SEQUENCE = ByteSequenceWrapper.of("o");
    public static final ByteSequence BID_SPECIALIST = ByteSequenceWrapper.of("p");
    public static final ByteSequence OFFER_SPECIALIST = ByteSequenceWrapper.of("q");
    public static final ByteSequence BID_OFFER_SPECIALIST = ByteSequenceWrapper.of("r");
    public static final ByteSequence END_OF_DAY_SAM = ByteSequenceWrapper.of("s");
    public static final ByteSequence FORBIDDEN_SAM = ByteSequenceWrapper.of("t");
    public static final ByteSequence FROZEN_SAM = ByteSequenceWrapper.of("u");
    public static final ByteSequence PREOPENING_SAM = ByteSequenceWrapper.of("v");
    public static final ByteSequence OPENING_SAM = ByteSequenceWrapper.of("w");
    public static final ByteSequence OPEN_SAM = ByteSequenceWrapper.of("x");
    public static final ByteSequence SURVEILLANCE_SAM = ByteSequenceWrapper.of("y");
    public static final ByteSequence SUSPENDED_SAM = ByteSequenceWrapper.of("z");
    public static final ByteSequence NO_MARKET_ACTIVITY = ByteSequenceWrapper.of("1000");
    public static final ByteSequence NO_DATA_AVAILABLE = ByteSequenceWrapper.of("1001");
    public static final ByteSequence NOT_APPLICABLE = ByteSequenceWrapper.of("1002");
    public static final ByteSequence AMOUNT_THRESHOLD_EXCEEDED = ByteSequenceWrapper.of("1003");

}