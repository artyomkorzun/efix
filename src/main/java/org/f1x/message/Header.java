package org.f1x.message;

import org.f1x.util.ByteArrayReference;

public class Header {

    protected final ByteArrayReference msgType = new ByteArrayReference();
    protected int msgSeqNum;
    protected boolean possDup;

    public ByteArrayReference getMsgType() {
        return msgType;
    }

    public int msgSeqNum() {
        return msgSeqNum;
    }

    public void msgSeqNum(int msgSeqNum) {
        this.msgSeqNum = msgSeqNum;
    }

    public boolean possDup() {
        return possDup;
    }

    public void possDup(boolean possDup) {
        this.possDup = possDup;
    }

}
