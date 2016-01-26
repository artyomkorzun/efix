package org.f1x.state;

public class MemorySessionState extends AbstractSessionState {

    protected long sessionStartTime = Long.MIN_VALUE;
    protected int senderSeqNum = 1;
    protected int targetSeqNum = 1;

    @Override
    public int senderSeqNum() {
        return senderSeqNum;
    }

    @Override
    public void senderSeqNum(int seqNum) {
        this.senderSeqNum = seqNum;
    }

    @Override
    public int targetSeqNum() {
        return targetSeqNum;
    }

    @Override
    public void targetSeqNum(int seqNum) {
        this.targetSeqNum = seqNum;
    }

    @Override
    public long sessionStartTime() {
        return sessionStartTime;
    }

    @Override
    public void sessionStartTime(long time) {
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
