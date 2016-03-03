package org.efix.message.field;


public class MassCancelRequestType {

    public static final byte CANCEL_ORDERS_FOR_A_SECURITY = '1';
    public static final byte CANCEL_ORDERS_FOR_AN_UNDERLYING_SECURITY = '2';
    public static final byte CANCEL_ORDERS_FOR_A_PRODUCT = '3';
    public static final byte CANCEL_ORDERS_FOR_A_CFICODE = '4';
    public static final byte CANCEL_ORDERS_FOR_A_SECURITYTYPE = '5';
    public static final byte CANCEL_ORDERS_FOR_A_TRADING_SESSION = '6';
    public static final byte CANCEL_ALL_ORDERS = '7';

}