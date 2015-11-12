package org.f1x.state;

import org.f1x.SessionComponent;


public interface SessionState extends SessionComponent {

    SessionStatus getStatus();

    void setStatus(SessionStatus status);


    boolean isSeqNumsSynchronized();

    void setSeqNumsSynchronized(boolean seqNumsSynchronized);


    int getNextSenderSeqNum();

    void setNextSenderSeqNum(int newValue);


    int getNextTargetSeqNum();

    void setNextTargetSeqNum(int newValue);


    long getSessionStartTime();

    void setSessionStartTime(long time);


    long getLastReceivedTime();

    void setLastReceivedTime(long time);


    long getLastSentTime();

    void setLastSentTime(long time);


    boolean isTestRequestSent();

    void setTestRequestSent(boolean sent);

}
