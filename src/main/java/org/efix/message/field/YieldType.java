package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class YieldType {

    public static final ByteSequence YIELD_TO_NEXT_PUT = ByteSequenceWrapper.of("PUT");
    public static final ByteSequence BOOK_YIELD = ByteSequenceWrapper.of("BOOK");
    public static final ByteSequence YIELD_TO_NEXT_CALL = ByteSequenceWrapper.of("CALL");
    public static final ByteSequence MARK_TO_MARKET_YIELD = ByteSequenceWrapper.of("MARK");
    public static final ByteSequence TRUE_YIELD = ByteSequenceWrapper.of("TRUE");
    public static final ByteSequence CLOSING_YIELD = ByteSequenceWrapper.of("CLOSE");
    public static final ByteSequence TRUE_GROSS_YIELD = ByteSequenceWrapper.of("GROSS");
    public static final ByteSequence YIELD_TO_WORST = ByteSequenceWrapper.of("WORST");
    public static final ByteSequence ANNUAL_YIELD = ByteSequenceWrapper.of("ANNUAL");
    public static final ByteSequence YIELD_CHANGE_SINCE_CLOSE = ByteSequenceWrapper.of("CHANGE");
    public static final ByteSequence SIMPLE_YIELD = ByteSequenceWrapper.of("SIMPLE");
    public static final ByteSequence YIELD_TO_TENDER_DATE = ByteSequenceWrapper.of("TENDER");
    public static final ByteSequence YIELD_AT_ISSUE = ByteSequenceWrapper.of("ATISSUE");
    public static final ByteSequence CURRENT_YIELD = ByteSequenceWrapper.of("CURRENT");
    public static final ByteSequence OPEN_AVERAGE_YIELD = ByteSequenceWrapper.of("OPENAVG");
    public static final ByteSequence AFTER_TAX_YIELD = ByteSequenceWrapper.of("AFTERTAX");
    public static final ByteSequence COMPOUND_YIELD = ByteSequenceWrapper.of("COMPOUND");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_YEAR = ByteSequenceWrapper.of("LASTYEAR");
    public static final ByteSequence YIELD_TO_MATURITY = ByteSequenceWrapper.of("MATURITY");
    public static final ByteSequence PROCEEDS_YIELD = ByteSequenceWrapper.of("PROCEEDS");
    public static final ByteSequence TAX_EQUIVALENT_YIELD = ByteSequenceWrapper.of("TAXEQUIV");
    public static final ByteSequence GOVERNMENT_EQUIVALENT_YIELD = ByteSequenceWrapper.of("GOVTEQUIV");
    public static final ByteSequence YIELD_WITH_INFLATION_ASSUMPTION = ByteSequenceWrapper.of("INFLATION");
    public static final ByteSequence MOST_RECENT_CLOSING_YIELD = ByteSequenceWrapper.of("LASTCLOSE");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_MONTH = ByteSequenceWrapper.of("LASTMONTH");
    public static final ByteSequence PREVIOUS_CLOSE_YIELD = ByteSequenceWrapper.of("PREVCLOSE");
    public static final ByteSequence YIELD_VALUE_OF_1_32 = ByteSequenceWrapper.of("VALUE1_32");
    public static final ByteSequence YIELD_TO_NEXT_REFUND = ByteSequenceWrapper.of("NEXTREFUND");
    public static final ByteSequence SEMI_ANNUAL_YIELD = ByteSequenceWrapper.of("SEMIANNUAL");
    public static final ByteSequence YIELD_TO_AVERAGE_MATURITY = ByteSequenceWrapper.of("AVGMATURITY");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_QUARTER = ByteSequenceWrapper.of("LASTQUARTER");
    public static final ByteSequence YIELD_TO_LONGEST_AVERAGE_LIFE = ByteSequenceWrapper.of("LONGAVGLIFE");
    public static final ByteSequence YIELD_TO_SHORTEST_AVERAGE_LIFE = ByteSequenceWrapper.of("SHORTAVGLIFE");
    public static final ByteSequence INVERSE_FLOATER_BOND_YIELD = ByteSequenceWrapper.of("INVERSEFLOATER");

}