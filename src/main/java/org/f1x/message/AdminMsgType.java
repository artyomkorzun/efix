package org.f1x.message;

import org.f1x.util.ByteSequence;


public class AdminMsgType {

    public static final byte LOGON = 'A';
    public static final byte HEARTBEAT = '0';
    public static final byte TEST = '1';
    public static final byte RESEND = '2';
    public static final byte REJECT = '3';
    public static final byte RESET = '4';
    public static final byte LOGOUT = '5';

    public static boolean isAdmin(ByteSequence msgType) {
        if (msgType.length() != 1)
            return false;

        byte type = msgType.byteAt(0);
        return HEARTBEAT <= type && type <= LOGOUT ||
                type == LOGON;
    }

}
