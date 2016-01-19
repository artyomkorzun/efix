package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class StipulationType {

    public static final ByteSequence ABSOLUTE_PREPAYMENT_SPEED = of("ABS");
    public static final ByteSequence AMT = of("AMT");
    public static final ByteSequence CONSTANT_PREPAYMENT_PENALTY = of("CPP");
    public static final ByteSequence CONSTANT_PREPAYMENT_RATE = of("CPR");
    public static final ByteSequence CONSTANT_PREPAYMENT_YIELD = of("CPY");
    public static final ByteSequence FINAL_CPR_OF_HOME_EQUITY_PREPAYMENT_CURVE = of("HEP");
    public static final ByteSequence EXPLICIT_LOT_IDENTIFIER = of("LOT");
    public static final ByteSequence MATURITY_YEAR_AND_MONTH = of("MAT");
    public static final ByteSequence PERCENT_OF_MANUFACTURED_HOUSING_PREPAYMENT_CURVE = of("MHP");
    public static final ByteSequence MONTHLY_PREPAYMENT_RATE = of("MPR");
    public static final ByteSequence PERCENT_OF_PROSPECTUS_PREPAYMENT_CURVE = of("PPC");
    public static final ByteSequence POOLS_PER_LOT = of("PPL");
    public static final ByteSequence POOLS_PER_MILLION = of("PPM");
    public static final ByteSequence POOLS_PER_TRADE = of("PPT");
    public static final ByteSequence PERCENT_OF_BMA_PREPAYMENT_CURVE = of("PSA");
    public static final ByteSequence SINGLE_MONTHLY_MORTALITY = of("SMM");
    public static final ByteSequence WEIGHTED_AVERAGE_COUPON = of("WAC");
    public static final ByteSequence WEIGHTED_AVERAGE_LIFE_COUPON = of("WAL");
    public static final ByteSequence WEIGHTED_AVERAGE_MATURITY = of("WAM");
    public static final ByteSequence GEOGRAPHICS_AND_PERCENT_RANGE = of("GEOG");
    public static final ByteSequence POOLS_MAXIMUM = of("PMAX");
    public static final ByteSequence PRODUCTION_YEAR = of("PROD");
    public static final ByteSequence FREEFORM_TEXT = of("TEXT");
    public static final ByteSequence WEIGHTED_AVERAGE_LOAN_AGE = of("WALA");
    public static final ByteSequence YEAR_OR_YEAR_MONTH_OF_ISSUE = of("ISSUE");
    public static final ByteSequence PRICE_RANGE = of("PRICE");
    public static final ByteSequence WHOLE_POOL = of("WHOLE");
    public static final ByteSequence YIELD_RANGE = of("YIELD");
    public static final ByteSequence BARGAIN_CONDITIONS = of("BGNCON");
    public static final ByteSequence COUPON_RANGE = of("COUPON");
    public static final ByteSequence ISSUERS_TICKER = of("ISSUER");
    public static final ByteSequence LOT_VARIANCE = of("LOTVAR");
    public static final ByteSequence MINIMUM_QUANTITY = of("MINQTY");
    public static final ByteSequence NUMBER_OF_PIECES = of("PIECES");
    public static final ByteSequence RATING_SOURCE_AND_RANGE = of("RATING");
    public static final ByteSequence MARKET_SECTOR = of("SECTOR");
    public static final ByteSequence STRUCTURE = of("STRUCT");
    public static final ByteSequence TRADE_VARIANCE = of("TRDVAR");
    public static final ByteSequence VALUATION_DISCOUNT = of("HAIRCUT");
    public static final ByteSequence INSURED = of("INSURED");
    public static final ByteSequence MAXIMUM_SUBSTITUTIONS = of("MAXSUBS");
    public static final ByteSequence MINIMUM_DENOMINATION = of("MINDNOM");
    public static final ByteSequence MINIMUM_INCREMENT = of("MININCR");
    public static final ByteSequence PAYMENT_FREQUENCY_CALENDAR = of("PAYFREQ");
    public static final ByteSequence CALL_PROTECTION = of("PROTECT");
    public static final ByteSequence PURPOSE = of("PURPOSE");
    public static final ByteSequence SECURITYTYPE_INCLUDED_OR_EXCLUDED = of("SECTYPE");
    public static final ByteSequence BANK_QUALIFIED = of("BANKQUAL");
    public static final ByteSequence ISO_CURRENCY_CODE = of("CURRENCY");
    public static final ByteSequence LOOKBACK_DAYS = of("LOOKBACK");
    public static final ByteSequence MATURITY_RANGE = of("MATURITY");
    public static final ByteSequence BENCHMARK_PRICE_SOURCE = of("PXSOURCE");
    public static final ByteSequence SUBSTITUTIONS_FREQUENCY = of("SUBSFREQ");
    public static final ByteSequence SUBSTITUTIONS_LEFT = of("SUBSLEFT");
    public static final ByteSequence AUTO_REINVESTMENT_AT_OR_BETTER = of("AUTOREINV");
    public static final ByteSequence ISSUE_SIZE_RANGE = of("ISSUESIZE");
    public static final ByteSequence PRICING_FREQUENCY = of("PRICEFREQ");
    public static final ByteSequence CUSTOM_START_END_DATE = of("CUSTOMDATE");
    public static final ByteSequence TYPE_OF_REDEMPTION = of("REDEMPTION");
    public static final ByteSequence RESTRICTED = of("RESTRICTED");

}