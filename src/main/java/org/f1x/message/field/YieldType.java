package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class YieldType {

    public static final ByteSequence YIELD_TO_NEXT_PUT = of("PUT");
    public static final ByteSequence BOOK_YIELD = of("BOOK");
    public static final ByteSequence YIELD_TO_NEXT_CALL = of("CALL");
    public static final ByteSequence MARK_TO_MARKET_YIELD = of("MARK");
    public static final ByteSequence TRUE_YIELD = of("TRUE");
    public static final ByteSequence CLOSING_YIELD = of("CLOSE");
    public static final ByteSequence TRUE_GROSS_YIELD = of("GROSS");
    public static final ByteSequence YIELD_TO_WORST = of("WORST");
    public static final ByteSequence ANNUAL_YIELD = of("ANNUAL");
    public static final ByteSequence YIELD_CHANGE_SINCE_CLOSE = of("CHANGE");
    public static final ByteSequence SIMPLE_YIELD = of("SIMPLE");
    public static final ByteSequence YIELD_TO_TENDER_DATE = of("TENDER");
    public static final ByteSequence YIELD_AT_ISSUE = of("ATISSUE");
    public static final ByteSequence CURRENT_YIELD = of("CURRENT");
    public static final ByteSequence OPEN_AVERAGE_YIELD = of("OPENAVG");
    public static final ByteSequence AFTER_TAX_YIELD = of("AFTERTAX");
    public static final ByteSequence COMPOUND_YIELD = of("COMPOUND");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_YEAR = of("LASTYEAR");
    public static final ByteSequence YIELD_TO_MATURITY = of("MATURITY");
    public static final ByteSequence PROCEEDS_YIELD = of("PROCEEDS");
    public static final ByteSequence TAX_EQUIVALENT_YIELD = of("TAXEQUIV");
    public static final ByteSequence GOVERNMENT_EQUIVALENT_YIELD = of("GOVTEQUIV");
    public static final ByteSequence YIELD_WITH_INFLATION_ASSUMPTION = of("INFLATION");
    public static final ByteSequence MOST_RECENT_CLOSING_YIELD = of("LASTCLOSE");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_MONTH = of("LASTMONTH");
    public static final ByteSequence PREVIOUS_CLOSE_YIELD = of("PREVCLOSE");
    public static final ByteSequence YIELD_VALUE_OF_1_32 = of("VALUE1_32");
    public static final ByteSequence YIELD_TO_NEXT_REFUND = of("NEXTREFUND");
    public static final ByteSequence SEMI_ANNUAL_YIELD = of("SEMIANNUAL");
    public static final ByteSequence YIELD_TO_AVERAGE_MATURITY = of("AVGMATURITY");
    public static final ByteSequence CLOSING_YIELD_MOST_RECENT_QUARTER = of("LASTQUARTER");
    public static final ByteSequence YIELD_TO_LONGEST_AVERAGE_LIFE = of("LONGAVGLIFE");
    public static final ByteSequence YIELD_TO_SHORTEST_AVERAGE_LIFE = of("SHORTAVGLIFE");
    public static final ByteSequence INVERSE_FLOATER_BOND_YIELD = of("INVERSEFLOATER");

}