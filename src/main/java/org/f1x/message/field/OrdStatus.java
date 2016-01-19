package org.f1x.message.field;


public class OrdStatus {

    public static final byte NEW = '0';
    public static final byte PARTIALLY_FILLED = '1';
    public static final byte FILLED = '2';
    public static final byte DONE_FOR_DAY = '3';
    public static final byte CANCELED = '4';
    public static final byte REPLACED = '5';
    public static final byte PENDING_CANCEL = '6';
    public static final byte STOPPED = '7';
    public static final byte REJECTED = '8';
    public static final byte SUSPENDED = '9';
    public static final byte PENDING_NEW = 'A';
    public static final byte CALCULATED = 'B';
    public static final byte EXPIRED = 'C';
    public static final byte ACCEPTED_FOR_BIDDING = 'D';
    public static final byte PENDING_REPLACE = 'E';

}