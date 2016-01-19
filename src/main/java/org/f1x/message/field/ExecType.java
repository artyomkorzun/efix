package org.f1x.message.field;


public class ExecType {

    public static final byte NEW = '0';
    public static final byte PARTIAL_FILL = '1';
    public static final byte FILL = '2';
    public static final byte DONE_FOR_DAY = '3';
    public static final byte CANCELED = '4';
    public static final byte REPLACE = '5';
    public static final byte PENDING_CANCEL = '6';
    public static final byte STOPPED = '7';
    public static final byte REJECTED = '8';
    public static final byte SUSPENDED = '9';
    public static final byte PENDING_NEW = 'A';
    public static final byte CALCULATED = 'B';
    public static final byte EXPIRED = 'C';
    public static final byte RESTATED = 'D';
    public static final byte PENDING_REPLACE = 'E';
    public static final byte TRADE = 'F';
    public static final byte TRADE_CORRECT = 'G';
    public static final byte TRADE_CANCEL = 'H';
    public static final byte ORDER_STATUS = 'I';
    public static final byte TRADE_IN_A_CLEARING_HOLD = 'J';
    public static final byte TRADE_HAS_BEEN_RELEASED_TO_CLEARING = 'K';
    public static final byte TRIGGERED_OR_ACTIVATED_BY_SYSTEM = 'L';

}