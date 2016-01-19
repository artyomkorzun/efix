package org.f1x.message.field;


public class BookingUnit {

    public static final byte EACH_PARTIAL_EXECUTION_IS_A_BOOKABLE_UNIT = '0';
    public static final byte AGGREGATE_PARTIAL_EXECUTIONS_ON_THIS_ORDER_AND_BOOK_ONE_TRADE_PER_ORDER = '1';
    public static final byte AGGREGATE_EXECUTIONS_FOR_THIS_SYMBOL_SIDE_AND_SETTLEMENT_DATE = '2';

}