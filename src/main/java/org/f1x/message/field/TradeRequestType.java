package org.f1x.message.field;


public class TradeRequestType {

    public static final int ALL_TRADES = 0;
    public static final int MATCHED_TRADES_MATCHING_CRITERIA_PROVIDED_ON_REQUEST = 1;
    public static final int UNMATCHED_TRADES_THAT_MATCH_CRITERIA = 2;
    public static final int UNREPORTED_TRADES_THAT_MATCH_CRITERIA = 3;
    public static final int ADVISORIES_THAT_MATCH_CRITERIA = 4;

}