package org.efix.message;

import org.efix.util.ByteSequenceWrapper;


public class Header {

    protected final ByteSequenceWrapper beginString = new ByteSequenceWrapper();
    protected final ByteSequenceWrapper msgType = new ByteSequenceWrapper();

    protected final ByteSequenceWrapper senderCompId = new ByteSequenceWrapper();
    protected final ByteSequenceWrapper targetCompId = new ByteSequenceWrapper();

    protected long sendingTime;
    protected long originalSendingTime;

    protected int msgSeqNum;
    protected boolean possDup;

    public ByteSequenceWrapper beginString() {
        return beginString;
    }

    public ByteSequenceWrapper msgType() {
        return msgType;
    }

    public ByteSequenceWrapper senderCompId() {
        return senderCompId;
    }

    public ByteSequenceWrapper targetCompId() {
        return targetCompId;
    }

    public long sendingTime() {
        return sendingTime;
    }

    public Header sendingTime(long sendingTime) {
        this.sendingTime = sendingTime;
        return this;
    }

    public long originalSendingTime() {
        return originalSendingTime;
    }

    public Header originalSendingTime(long originalSendingTime) {
        this.originalSendingTime = originalSendingTime;
        return this;
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
