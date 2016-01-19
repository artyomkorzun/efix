package org.f1x.message.field;


public class SubscriptionRequestType {

    public static final byte SNAPSHOT = '0';
    public static final byte SNAPSHOT_PLUS_UPDATES = '1';
    public static final byte DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST = '2';

}