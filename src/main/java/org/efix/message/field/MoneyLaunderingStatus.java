package org.efix.message.field;


public class MoneyLaunderingStatus {

    public static final byte EXEMPT_BELOW_THE_LIMIT = '1';
    public static final byte EXEMPT_CLIENT_MONEY_TYPE_EXEMPTION = '2';
    public static final byte EXEMPT_AUTHORISED_CREDIT_OR_FINANCIAL_INSTITUTION = '3';
    public static final byte NOT_CHECKED = 'N';
    public static final byte PASSED = 'Y';

}