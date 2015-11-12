package org.f1x.state;

public abstract class AbstractSessionState implements SessionState {

    protected SessionStatus status = SessionStatus.DISCONNECTED;
    protected long lastReceivedTime = Long.MIN_VALUE;
    protected long lastSentTime = Long.MIN_VALUE;
    protected boolean seqNumsSynchronized;
    protected boolean isTestRequestSent;

    @Override
    public SessionStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    @Override
    public boolean isSeqNumsSynchronized() {
        return seqNumsSynchronized;
    }

    @Override
    public void setSeqNumsSynchronized(boolean seqNumsSynchronized) {
        this.seqNumsSynchronized = seqNumsSynchronized;
    }

    @Override
    public long getLastReceivedTime() {
        return lastReceivedTime;
    }

    @Override
    public void setLastReceivedTime(long time) {
        this.lastReceivedTime = time;
    }

    @Override
    public long getLastSentTime() {
        return lastSentTime;
    }

    @Override
    public void setLastSentTime(long time) {
        this.lastSentTime = time;
    }

    @Override
    public boolean isTestRequestSent() {
        return isTestRequestSent;
    }

    @Override
    public void setTestRequestSent(boolean sent) {
        this.isTestRequestSent = sent;
    }

}
