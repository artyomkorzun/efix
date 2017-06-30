package org.efix.message;

import org.efix.util.ByteSequenceWrapper;


public class Header {

    protected final ByteSequenceWrapper msgType = new ByteSequenceWrapper();

    protected int msgSeqNum;
    protected boolean possDup;

    public ByteSequenceWrapper msgType() {
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
