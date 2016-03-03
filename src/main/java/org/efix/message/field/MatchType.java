package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class MatchType {

    public static final ByteSequence ONE_PARTY_TRADE_REPORT = ByteSequenceWrapper.of("1");
    public static final ByteSequence TWO_PARTY_TRADE_REPORT = ByteSequenceWrapper.of("2");
    public static final ByteSequence CONFIRMED_TRADE_REPORT = ByteSequenceWrapper.of("3");
    public static final ByteSequence AUTO_MATCH = ByteSequenceWrapper.of("4");
    public static final ByteSequence CROSS_AUCTION = ByteSequenceWrapper.of("5");
    public static final ByteSequence COUNTER_ORDER_SELECTION = ByteSequenceWrapper.of("6");
    public static final ByteSequence CALL_AUCTION = ByteSequenceWrapper.of("7");
    public static final ByteSequence ONE_PARTY_PRIVATELY_NEGOTIATED_TRADE_REPORT = ByteSequenceWrapper.of("60");
    public static final ByteSequence TWO_PARTY_PRIVATELY_NEGOTIATED_TRADE_REPORT = ByteSequenceWrapper.of("61");
    public static final ByteSequence CONTINUOUS_AUTO_MATCH = ByteSequenceWrapper.of("62");
    public static final ByteSequence CROSS_AUCTION2 = ByteSequenceWrapper.of("63");
    public static final ByteSequence COUNTER_ORDER_SELECTION2 = ByteSequenceWrapper.of("64");
    public static final ByteSequence CALL_AUCTION2 = ByteSequenceWrapper.of("65");
    public static final ByteSequence EXACT_PLUS_FOUR_BADGES_AND_EXECUTION_TIME = ByteSequenceWrapper.of("A1");
    public static final ByteSequence EXACT_PLUS_FOUR_BADGES = ByteSequenceWrapper.of("A2");
    public static final ByteSequence EXACT_PLUS_TWO_BADGES_AND_EXECUTION_TIME = ByteSequenceWrapper.of("A3");
    public static final ByteSequence EXACT_PLUS_TWO_BADGES = ByteSequenceWrapper.of("A4");
    public static final ByteSequence EXACT_PLUS_EXECUTION_TIME = ByteSequenceWrapper.of("A5");
    public static final ByteSequence COMPARED_RECORDS_RESULTING_FROM_STAMPED_ADVISORIES_OR_SPECIALIST_ACCEPTS_PAIR_OFFS = ByteSequenceWrapper.of("AQ");
    public static final ByteSequence EXACT_MATCH_ON_TRADE_DATE_STOCK_SYMBOL_QUANTITY_PRICE_TRADE_TYPE_AND_SPECIAL_TRADE_INDICATOR_MINUS_BADGES_AND_TIMES_ACT_M1_MATCH = ByteSequenceWrapper.of("M1");
    public static final ByteSequence SUMMARIZED_MATCH_MINUS_BADGES_AND_TIMES_ACT_M2_MATCH = ByteSequenceWrapper.of("M2");
    public static final ByteSequence ACT_ACCEPTED_TRADE = ByteSequenceWrapper.of("M3");
    public static final ByteSequence ACT_DEFAULT_TRADE = ByteSequenceWrapper.of("M4");
    public static final ByteSequence ACT_DEFAULT_AFTER_M2 = ByteSequenceWrapper.of("M5");
    public static final ByteSequence ACT_M6_MATCH = ByteSequenceWrapper.of("M6");
    public static final ByteSequence OCS_LOCKED_IN_NON_ACT = ByteSequenceWrapper.of("MT");
    public static final ByteSequence SUMMARIZED_MATCH_USING_A1_EXACT_MATCH_CRITERIA_EXCEPT_QUANTITY_IS_SUMMARIED = ByteSequenceWrapper.of("S1");
    public static final ByteSequence SUMMARIZED_MATCH_USING_A2_EXACT_MATCH_CRITERIA_EXCEPT_QUANTITY_IS_SUMMARIZED = ByteSequenceWrapper.of("S2");
    public static final ByteSequence SUMMARIZED_MATCH_USING_A3_EXACT_MATCH_CRITERIA_EXCEPT_QUANTITY_IS_SUMMARIZED = ByteSequenceWrapper.of("S3");
    public static final ByteSequence SUMMARIZED_MATCH_USING_A4_EXACT_MATCH_CRITERIA_EXCEPT_QUANTITY_IS_SUMMARIZED = ByteSequenceWrapper.of("S4");
    public static final ByteSequence SUMMARIZED_MATCH_USING_A5_EXACT_MATCH_CRITERIA_EXCEPT_QUANTITY_IS_SUMMARIZED = ByteSequenceWrapper.of("S5");

}