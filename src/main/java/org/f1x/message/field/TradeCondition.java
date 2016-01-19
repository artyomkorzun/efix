package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class TradeCondition {

    public static final ByteSequence CANCEL = of("0");
    public static final ByteSequence CASH_MARKET = of("A");
    public static final ByteSequence AVERAGE_PRICE_TRADE = of("B");
    public static final ByteSequence CASH_TRADE = of("C");
    public static final ByteSequence NEXT_DAY_MARKET = of("D");
    public static final ByteSequence OPENING_REOPENING_TRADE_DETAIL = of("E");
    public static final ByteSequence INTRADAY_TRADE_DETAIL = of("F");
    public static final ByteSequence RULE127 = of("G");
    public static final ByteSequence RULE155 = of("H");
    public static final ByteSequence SOLD_LAST = of("I");
    public static final ByteSequence NEXT_DAY_TRADE = of("J");
    public static final ByteSequence OPENED = of("K");
    public static final ByteSequence SELLER = of("L");
    public static final ByteSequence SOLD = of("M");
    public static final ByteSequence STOPPED_STOCK = of("N");
    public static final ByteSequence IMBALANCE_MORE_BUYERS = of("P");
    public static final ByteSequence IMBALANCE_MORE_SELLERS = of("Q");
    public static final ByteSequence OPENING_PRICE = of("R");
    public static final ByteSequence BARGAIN_CONDITION = of("S");
    public static final ByteSequence CONVERTED_PRICE_INDICATOR = of("T");
    public static final ByteSequence EXCHANGE_LAST = of("U");
    public static final ByteSequence FINAL_PRICE_OF_SESSION = of("V");
    public static final ByteSequence EX_PIT = of("W");
    public static final ByteSequence CROSSED = of("X");
    public static final ByteSequence TRADES_RESULTING_FROM_MANUAL_SLOW_QUOTE = of("Y");
    public static final ByteSequence TRADES_RESULTING_FROM_INTERMARKET_SWEEP = of("Z");
    public static final ByteSequence VOLUME_ONLY = of("a");
    public static final ByteSequence DIRECT_PLUS = of("b");
    public static final ByteSequence ACQUISITION = of("c");
    public static final ByteSequence BUNCHED = of("d");
    public static final ByteSequence DISTRIBUTION = of("e");
    public static final ByteSequence BUNCHED_SALE = of("f");
    public static final ByteSequence SPLIT_TRADE = of("g");
    public static final ByteSequence CANCEL_STOPPED = of("h");
    public static final ByteSequence CANCEL_ETH = of("i");
    public static final ByteSequence CANCEL_STOPPED_ETH = of("j");
    public static final ByteSequence OUT_OF_SEQUENCE_ETH = of("k");
    public static final ByteSequence CANCEL_LAST_ETH = of("l");
    public static final ByteSequence SOLD_LAST_SALE_ETH = of("m");
    public static final ByteSequence CANCEL_LAST = of("n");
    public static final ByteSequence SOLD_LAST_SALE = of("o");
    public static final ByteSequence CANCEL_OPEN = of("p");
    public static final ByteSequence CANCEL_OPEN_ETH = of("q");
    public static final ByteSequence OPENED_SALE_ETH = of("r");
    public static final ByteSequence CANCEL_ONLY = of("s");
    public static final ByteSequence CANCEL_ONLY_ETH = of("t");
    public static final ByteSequence LATE_OPEN_ETH = of("u");
    public static final ByteSequence AUTO_EXECUTION_ETH = of("v");
    public static final ByteSequence REOPEN = of("w");
    public static final ByteSequence REOPEN_ETH = of("x");
    public static final ByteSequence ADJUSTED = of("y");
    public static final ByteSequence ADJUSTED_ETH = of("z");
    public static final ByteSequence SPREAD = of("AA");
    public static final ByteSequence SPREAD_ETH = of("AB");
    public static final ByteSequence STRADDLE = of("AC");
    public static final ByteSequence STRADDLE_ETH = of("AD");
    public static final ByteSequence STOPPED = of("AE");
    public static final ByteSequence STOPPED_ETH = of("AF");
    public static final ByteSequence REGULAR_ETH = of("AG");
    public static final ByteSequence COMBO = of("AH");
    public static final ByteSequence COMBO_ETH = of("AI");
    public static final ByteSequence OFFICIAL_CLOSING_PRICE = of("AJ");
    public static final ByteSequence PRIOR_REFERENCE_PRICE = of("AK");
    public static final ByteSequence STOPPED_SOLD_LAST = of("AL");
    public static final ByteSequence STOPPED_OUT_OF_SEQUENCE = of("AM");
    public static final ByteSequence OFFICAL_CLOSING_PRICE = of("AN");
    public static final ByteSequence CROSSED2 = of("AO");
    public static final ByteSequence FAST_MARKET = of("AP");
    public static final ByteSequence AUTOMATIC_EXECUTION = of("AQ");
    public static final ByteSequence FORM_T = of("AR");
    public static final ByteSequence BASKET_INDEX = of("AS");
    public static final ByteSequence BURST_BASKET = of("AT");
    public static final ByteSequence NO_MARKET_ACTIVITY = of("1000");
    public static final ByteSequence NO_DATA_AVAILABLE = of("1001");
    public static final ByteSequence NOT_APPLICABLE = of("1002");

}