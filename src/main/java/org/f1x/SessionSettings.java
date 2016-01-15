package org.f1x;


public class SessionSettings {

    protected FIXVersion fixVersion;
    protected SessionID sessionID;
    protected boolean initiator = true;
    protected int heartbeatInterval = 30;
    protected int heartbeatTimeout = 1000 * (heartbeatInterval + 1);
    protected int logonTimeout = 2000;
    protected int logoutTimeout = 2000;
    protected boolean resetSeqNumsOnEachLogon;
    protected boolean logonWithNextExpectedSeqNum;

    public FIXVersion getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(FIXVersion fixVersion) {
        this.fixVersion = fixVersion;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTimeout = 1000 * (heartbeatInterval + 1);
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public void setLogoutTimeout(int logoutTimeout) {
        this.logoutTimeout = logoutTimeout;
    }

    public int getLogoutTimeout() {
        return logoutTimeout;
    }

    public int getLogonTimeout() {
        return logonTimeout;
    }

    public void setLogonTimeout(int logonTimeout) {
        this.logonTimeout = logonTimeout;
    }

    public boolean resetSeqNumsOnEachLogon() {
        return resetSeqNumsOnEachLogon;
    }

    public void resetSeqNumsOnEachLogon(boolean resetSeqNumsOnEachLogon) {
        this.resetSeqNumsOnEachLogon = resetSeqNumsOnEachLogon;
    }

    public boolean isLogonWithNextExpectedSeqNum() {
        return logonWithNextExpectedSeqNum;
    }

    public void setLogonWithNextExpectedSeqNum(boolean logonWithNextExpectedSeqNum) {
        this.logonWithNextExpectedSeqNum = logonWithNextExpectedSeqNum;
    }

}
