package org.efix.state;

import org.efix.SessionComponent;


public interface SessionState extends SessionComponent {

    SessionStatus status();

    void status(SessionStatus status);


    boolean targetSeqNumSynced();

    void targetSeqNumSynced(boolean synced);


    int senderSeqNum();

    void senderSeqNum(int seqNum);


    int targetSeqNum();

    void targetSeqNum(int seqNum);


    long sessionStartTime();

    void sessionStartTime(long time);


    long lastReceivedTime();

    void lastReceivedTime(long time);


    long lastSentTime();

    void lastSentTime(long time);


    boolean testRequestSent();

    void testRequestSent(boolean sent);

}
