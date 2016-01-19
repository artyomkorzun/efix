package org.f1x.message.field;


public class OrderRestrictions {

    public static final byte PROGRAM_TRADE = '1';
    public static final byte INDEX_ARBITRAGE = '2';
    public static final byte NON_INDEX_ARBITRAGE = '3';
    public static final byte COMPETING_MARKET_MAKER = '4';
    public static final byte ACTING_AS_MARKET_MAKER_OR_SPECIALIST_IN_THE_SECURITY = '5';
    public static final byte ACTING_AS_MARKET_MAKER_OR_SPECIALIST_IN_THE_UNDERLYING_SECURITY_OF_A_DERIVATIVE_SECURITY = '6';
    public static final byte FOREIGN_ENTITY = '7';
    public static final byte EXTERNAL_MARKET_PARTICIPANT = '8';
    public static final byte EXTERNAL_INTER_CONNECTED_MARKET_LINKAGE = '9';
    public static final byte RISKLESS_ARBITRAGE = 'A';

}