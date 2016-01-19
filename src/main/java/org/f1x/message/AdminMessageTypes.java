package org.f1x.message;

import org.f1x.util.BufferUtil;
import org.f1x.util.ByteSequence;
import org.f1x.util.ByteSequenceWrapper;

public class AdminMessageTypes {

    public static final byte LOGON = 'A';
    public static final byte HEARTBEAT = '0';
    public static final byte TEST = '1';
    public static final byte RESEND = '2';
    public static final byte REJECT = '3';
    public static final byte RESET = '4';
    public static final byte LOGOUT = '5';

    public static boolean isAdmin(CharSequence msgType) {
        if (msgType.length() != 1)
            return false;

        char charMsgType = msgType.charAt(0);
        return HEARTBEAT <= charMsgType && charMsgType <= LOGOUT ||
                charMsgType == LOGON;
    }

}

class Symbol {

    public static final ByteSequence EXCHANGE = ByteSequenceWrapper.of("dsds");

    private static ByteSequenceWrapper byteSequence(String value) {
        return new ByteSequenceWrapper(BufferUtil.fromString(value));
    }


}
