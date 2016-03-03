package org.efix.message;

public class Logon {

    protected int heartBtInt;
    protected boolean resetSeqNums;

    public int heartBtInt() {
        return heartBtInt;
    }

    public void heartBtInt(int heartBtInt) {
        this.heartBtInt = heartBtInt;
    }

    public boolean resetSeqNums() {
        return resetSeqNums;
    }

    public void resetSeqNums(boolean resetSeqNums) {
        this.resetSeqNums = resetSeqNums;
    }

}
