package org.efix.message;

public class ResendRequest {

    protected int beginSeqNo;
    protected int endSeqNo;

    public int beginSeqNo() {
        return beginSeqNo;
    }

    public void beginSeqNo(int beginSeqNo) {
        this.beginSeqNo = beginSeqNo;
    }

    public int endSeqNo() {
        return endSeqNo;
    }

    public void endSeqNo(int endSeqNo) {
        this.endSeqNo = endSeqNo;
    }

}
