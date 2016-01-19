package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class QuoteCondition {

    public static final ByteSequence RESERVED_SAM = of("0");
    public static final ByteSequence NO_ACTIVE_SAM = of("1");
    public static final ByteSequence RESTRICTED = of("2");
    public static final ByteSequence OPEN_ACTIVE = of("A");
    public static final ByteSequence CLOSED_INACTIVE = of("B");
    public static final ByteSequence EXCHANGE_BEST = of("C");
    public static final ByteSequence CONSOLIDATED_BEST = of("D");
    public static final ByteSequence LOCKED = of("E");
    public static final ByteSequence CROSSED = of("F");
    public static final ByteSequence DEPTH = of("G");
    public static final ByteSequence FAST_TRADING = of("H");
    public static final ByteSequence NON_FIRM = of("I");
    public static final ByteSequence OUTRIGHT_PRICE = of("J");
    public static final ByteSequence IMPLIED_PRICE = of("K");
    public static final ByteSequence MANUAL_SLOW_QUOTE = of("L");
    public static final ByteSequence DEPTH_ON_OFFER = of("M");
    public static final ByteSequence DEPTH_ON_BID = of("N");
    public static final ByteSequence CLOSING = of("O");
    public static final ByteSequence NEWS_DISSEMINATION = of("P");
    public static final ByteSequence TRADING_RANGE = of("Q");
    public static final ByteSequence ORDER_INFLUX = of("R");
    public static final ByteSequence DUE_TO_RELATED = of("S");
    public static final ByteSequence NEWS_PENDING = of("T");
    public static final ByteSequence ADDITIONAL_INFO = of("U");
    public static final ByteSequence ADDITIONAL_INFO_DUE_TO_RELATED = of("V");
    public static final ByteSequence RESUME = of("W");
    public static final ByteSequence VIEW_OF_COMMON = of("X");
    public static final ByteSequence VOLUME_ALERT = of("Y");
    public static final ByteSequence ORDER_IMBALANCE = of("Z");
    public static final ByteSequence EQUIPMENT_CHANGEOVER = of("a");
    public static final ByteSequence NO_OPEN = of("b");
    public static final ByteSequence REGULAR_ETH = of("c");
    public static final ByteSequence AUTOMATIC_EXECUTION = of("d");
    public static final ByteSequence AUTOMATIC_EXECUTION_ETH = of("e");
    public static final ByteSequence FAST_MARKET_ETH = of("f");
    public static final ByteSequence INACTIVE_ETH = of("g");
    public static final ByteSequence ROTATION = of("h");
    public static final ByteSequence ROTATION_ETH = of("i");
    public static final ByteSequence HALT = of("j");
    public static final ByteSequence HALT_ETH = of("k");
    public static final ByteSequence DUE_TO_NEWS_DISSEMINATION = of("l");
    public static final ByteSequence DUE_TO_NEWS_PENDING = of("m");
    public static final ByteSequence TRADING_RESUME = of("n");
    public static final ByteSequence OUT_OF_SEQUENCE = of("o");
    public static final ByteSequence BID_SPECIALIST = of("p");
    public static final ByteSequence OFFER_SPECIALIST = of("q");
    public static final ByteSequence BID_OFFER_SPECIALIST = of("r");
    public static final ByteSequence END_OF_DAY_SAM = of("s");
    public static final ByteSequence FORBIDDEN_SAM = of("t");
    public static final ByteSequence FROZEN_SAM = of("u");
    public static final ByteSequence PREOPENING_SAM = of("v");
    public static final ByteSequence OPENING_SAM = of("w");
    public static final ByteSequence OPEN_SAM = of("x");
    public static final ByteSequence SURVEILLANCE_SAM = of("y");
    public static final ByteSequence SUSPENDED_SAM = of("z");
    public static final ByteSequence NO_MARKET_ACTIVITY = of("1000");
    public static final ByteSequence NO_DATA_AVAILABLE = of("1001");
    public static final ByteSequence NOT_APPLICABLE = of("1002");
    public static final ByteSequence AMOUNT_THRESHOLD_EXCEEDED = of("1003");

}