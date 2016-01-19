package org.f1x.message.field;


public class ExecInst {

    public static final byte STAY_ON_OFFERSIDE = '0';
    public static final byte NOT_HELD = '1';
    public static final byte WORK = '2';
    public static final byte GO_ALONG = '3';
    public static final byte OVER_THE_DAY = '4';
    public static final byte HELD = '5';
    public static final byte PARTICIPATE_DONT_INITIATE = '6';
    public static final byte STRICT_SCALE = '7';
    public static final byte TRY_TO_SCALE = '8';
    public static final byte STAY_ON_BIDSIDE = '9';
    public static final byte NO_CROSS = 'A';
    public static final byte OK_TO_CROSS = 'B';
    public static final byte CALL_FIRST = 'C';
    public static final byte PERCENT_OF_VOLUME = 'D';
    public static final byte DO_NOT_INCREASE = 'E';
    public static final byte DO_NOT_REDUCE = 'F';
    public static final byte ALL_OR_NONE = 'G';
    public static final byte REINSTATE_ON_SYSTEM_FAILURE = 'H';
    public static final byte INSTITUTIONS_ONLY = 'I';
    public static final byte REINSTATE_ON_TRADING_HALT = 'J';
    public static final byte CANCEL_ON_TRADING_HALT = 'K';
    public static final byte LAST_PEG = 'L';
    public static final byte MID_PRICE = 'M';
    public static final byte NON_NEGOTIABLE = 'N';
    public static final byte OPENING_PEG = 'O';
    public static final byte MARKET_PEG = 'P';
    public static final byte CANCEL_ON_SYSTEM_FAILURE = 'Q';
    public static final byte PRIMARY_PEG = 'R';
    public static final byte SUSPEND = 'S';
    public static final byte FIXED_PEG_TO_LOCAL_BEST_BID_OR_OFFER_AT_TIME_OF_ORDER = 'T';
    public static final byte CUSTOMER_DISPLAY_INSTRUCTION = 'U';
    public static final byte NETTING = 'V';
    public static final byte PEG_TO_VWAP = 'W';
    public static final byte TRADE_ALONG = 'X';
    public static final byte TRY_TO_STOP = 'Y';
    public static final byte CANCEL_IF_NOT_BEST = 'Z';
    public static final byte TRAILING_STOP_PEG = 'a';
    public static final byte STRICT_LIMIT = 'b';
    public static final byte IGNORE_PRICE_VALIDITY_CHECKS = 'c';
    public static final byte PEG_TO_LIMIT_PRICE = 'd';
    public static final byte WORK_TO_TARGET_STRATEGY = 'e';
    public static final byte INTERMARKET_SWEEP = 'f';
    public static final byte EXTERNAL_ROUTING_ALLOWED = 'g';
    public static final byte EXTERNAL_ROUTING_NOT_ALLOWED = 'h';
    public static final byte IMBALANCE_ONLY = 'i';
    public static final byte SINGLE_EXECUTION_REQUESTED_FOR_BLOCK_TRADE = 'j';
    public static final byte BEST_EXECUTION = 'k';

}