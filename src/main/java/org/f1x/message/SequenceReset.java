package org.f1x.message;

public class SequenceReset {

    protected int newSeqNo;
    protected boolean gapFill;

    public int newSeqNo() {
        return newSeqNo;
    }

    public void newSeqNo(int newSeqNo) {
        this.newSeqNo = newSeqNo;
    }

    public boolean isGapFill() {
        return gapFill;
    }

    public void gapFill(boolean gapFill) {
        this.gapFill = gapFill;
    }

}
