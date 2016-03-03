package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class StipulationType {

    public static final ByteSequence ABSOLUTE_PREPAYMENT_SPEED = ByteSequenceWrapper.of("ABS");
    public static final ByteSequence AMT = ByteSequenceWrapper.of("AMT");
    public static final ByteSequence CONSTANT_PREPAYMENT_PENALTY = ByteSequenceWrapper.of("CPP");
    public static final ByteSequence CONSTANT_PREPAYMENT_RATE = ByteSequenceWrapper.of("CPR");
    public static final ByteSequence CONSTANT_PREPAYMENT_YIELD = ByteSequenceWrapper.of("CPY");
    public static final ByteSequence FINAL_CPR_OF_HOME_EQUITY_PREPAYMENT_CURVE = ByteSequenceWrapper.of("HEP");
    public static final ByteSequence EXPLICIT_LOT_IDENTIFIER = ByteSequenceWrapper.of("LOT");
    public static final ByteSequence MATURITY_YEAR_AND_MONTH = ByteSequenceWrapper.of("MAT");
    public static final ByteSequence PERCENT_OF_MANUFACTURED_HOUSING_PREPAYMENT_CURVE = ByteSequenceWrapper.of("MHP");
    public static final ByteSequence MONTHLY_PREPAYMENT_RATE = ByteSequenceWrapper.of("MPR");
    public static final ByteSequence PERCENT_OF_PROSPECTUS_PREPAYMENT_CURVE = ByteSequenceWrapper.of("PPC");
    public static final ByteSequence POOLS_PER_LOT = ByteSequenceWrapper.of("PPL");
    public static final ByteSequence POOLS_PER_MILLION = ByteSequenceWrapper.of("PPM");
    public static final ByteSequence POOLS_PER_TRADE = ByteSequenceWrapper.of("PPT");
    public static final ByteSequence PERCENT_OF_BMA_PREPAYMENT_CURVE = ByteSequenceWrapper.of("PSA");
    public static final ByteSequence SINGLE_MONTHLY_MORTALITY = ByteSequenceWrapper.of("SMM");
    public static final ByteSequence WEIGHTED_AVERAGE_COUPON = ByteSequenceWrapper.of("WAC");
    public static final ByteSequence WEIGHTED_AVERAGE_LIFE_COUPON = ByteSequenceWrapper.of("WAL");
    public static final ByteSequence WEIGHTED_AVERAGE_MATURITY = ByteSequenceWrapper.of("WAM");
    public static final ByteSequence GEOGRAPHICS_AND_PERCENT_RANGE = ByteSequenceWrapper.of("GEOG");
    public static final ByteSequence POOLS_MAXIMUM = ByteSequenceWrapper.of("PMAX");
    public static final ByteSequence PRODUCTION_YEAR = ByteSequenceWrapper.of("PROD");
    public static final ByteSequence FREEFORM_TEXT = ByteSequenceWrapper.of("TEXT");
    public static final ByteSequence WEIGHTED_AVERAGE_LOAN_AGE = ByteSequenceWrapper.of("WALA");
    public static final ByteSequence YEAR_OR_YEAR_MONTH_OF_ISSUE = ByteSequenceWrapper.of("ISSUE");
    public static final ByteSequence PRICE_RANGE = ByteSequenceWrapper.of("PRICE");
    public static final ByteSequence WHOLE_POOL = ByteSequenceWrapper.of("WHOLE");
    public static final ByteSequence YIELD_RANGE = ByteSequenceWrapper.of("YIELD");
    public static final ByteSequence BARGAIN_CONDITIONS = ByteSequenceWrapper.of("BGNCON");
    public static final ByteSequence COUPON_RANGE = ByteSequenceWrapper.of("COUPON");
    public static final ByteSequence ISSUERS_TICKER = ByteSequenceWrapper.of("ISSUER");
    public static final ByteSequence LOT_VARIANCE = ByteSequenceWrapper.of("LOTVAR");
    public static final ByteSequence MINIMUM_QUANTITY = ByteSequenceWrapper.of("MINQTY");
    public static final ByteSequence NUMBER_OF_PIECES = ByteSequenceWrapper.of("PIECES");
    public static final ByteSequence RATING_SOURCE_AND_RANGE = ByteSequenceWrapper.of("RATING");
    public static final ByteSequence MARKET_SECTOR = ByteSequenceWrapper.of("SECTOR");
    public static final ByteSequence STRUCTURE = ByteSequenceWrapper.of("STRUCT");
    public static final ByteSequence TRADE_VARIANCE = ByteSequenceWrapper.of("TRDVAR");
    public static final ByteSequence VALUATION_DISCOUNT = ByteSequenceWrapper.of("HAIRCUT");
    public static final ByteSequence INSURED = ByteSequenceWrapper.of("INSURED");
    public static final ByteSequence MAXIMUM_SUBSTITUTIONS = ByteSequenceWrapper.of("MAXSUBS");
    public static final ByteSequence MINIMUM_DENOMINATION = ByteSequenceWrapper.of("MINDNOM");
    public static final ByteSequence MINIMUM_INCREMENT = ByteSequenceWrapper.of("MININCR");
    public static final ByteSequence PAYMENT_FREQUENCY_CALENDAR = ByteSequenceWrapper.of("PAYFREQ");
    public static final ByteSequence CALL_PROTECTION = ByteSequenceWrapper.of("PROTECT");
    public static final ByteSequence PURPOSE = ByteSequenceWrapper.of("PURPOSE");
    public static final ByteSequence SECURITYTYPE_INCLUDED_OR_EXCLUDED = ByteSequenceWrapper.of("SECTYPE");
    public static final ByteSequence BANK_QUALIFIED = ByteSequenceWrapper.of("BANKQUAL");
    public static final ByteSequence ISO_CURRENCY_CODE = ByteSequenceWrapper.of("CURRENCY");
    public static final ByteSequence LOOKBACK_DAYS = ByteSequenceWrapper.of("LOOKBACK");
    public static final ByteSequence MATURITY_RANGE = ByteSequenceWrapper.of("MATURITY");
    public static final ByteSequence BENCHMARK_PRICE_SOURCE = ByteSequenceWrapper.of("PXSOURCE");
    public static final ByteSequence SUBSTITUTIONS_FREQUENCY = ByteSequenceWrapper.of("SUBSFREQ");
    public static final ByteSequence SUBSTITUTIONS_LEFT = ByteSequenceWrapper.of("SUBSLEFT");
    public static final ByteSequence AUTO_REINVESTMENT_AT_OR_BETTER = ByteSequenceWrapper.of("AUTOREINV");
    public static final ByteSequence ISSUE_SIZE_RANGE = ByteSequenceWrapper.of("ISSUESIZE");
    public static final ByteSequence PRICING_FREQUENCY = ByteSequenceWrapper.of("PRICEFREQ");
    public static final ByteSequence CUSTOM_START_END_DATE = ByteSequenceWrapper.of("CUSTOMDATE");
    public static final ByteSequence TYPE_OF_REDEMPTION = ByteSequenceWrapper.of("REDEMPTION");
    public static final ByteSequence RESTRICTED = ByteSequenceWrapper.of("RESTRICTED");

}