package org.f1x.state;

public class MemorySessionState extends AbstractSessionState {

    protected long sessionStartTime = Long.MIN_VALUE;
    protected int senderSeqNum = 1;
    protected int targetSeqNum = 1;

    @Override
    public int getNextSenderSeqNum() {
        return senderSeqNum;
    }

    @Override
    public void setNextSenderSeqNum(int newValue) {
        this.senderSeqNum = newValue;
    }

    @Override
    public int getNextTargetSeqNum() {
        return targetSeqNum;
    }

    @Override
    public void setNextTargetSeqNum(int newValue) {
        this.targetSeqNum = newValue;
    }

    @Override
    public long getSessionStartTime() {
        return sessionStartTime;
    }

    @Override
    public void setSessionStartTime(long time) {
        this.sessionStartTime = time;
    }

    @Override
    public void open() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

}
