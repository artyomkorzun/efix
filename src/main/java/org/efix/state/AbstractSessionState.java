package org.efix.state;

public abstract class AbstractSessionState implements SessionState {

    protected SessionStatus status = SessionStatus.DISCONNECTED;
    protected long lastReceivedTime = Long.MIN_VALUE;
    protected long lastSentTime = Long.MIN_VALUE;
    protected boolean synced;
    protected boolean testRequestSent;

    @Override
    public SessionStatus status() {
        return status;
    }

    @Override
    public void status(SessionStatus status) {
        this.status = status;
    }

    @Override
    public boolean synced() {
        return synced;
    }

    @Override
    public void synced(boolean synced) {
        this.synced = synced;
    }

    @Override
    public long lastReceivedTime() {
        return lastReceivedTime;
    }

    @Override
    public void lastReceivedTime(long time) {
        this.lastReceivedTime = time;
    }

    @Override
    public long lastSentTime() {
        return lastSentTime;
    }

    @Override
    public void lastSentTime(long time) {
        this.lastSentTime = time;
    }

    @Override
    public boolean testRequestSent() {
        return testRequestSent;
    }

    @Override
    public void testRequestSent(boolean sent) {
        this.testRequestSent = sent;
    }

}
