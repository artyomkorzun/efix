package org.efix.engine;

import org.efix.SessionType;
import org.junit.Test;

import static org.efix.state.SessionStatus.*;

public class SessionAcceptorTest extends SessionTest {

    public SessionAcceptorTest() {
        super(SessionType.ACCEPTOR);
    }

    // -------------------- LOGON --------------------

    @Test
    public void shouldDisconnectOnLogonWithoutHeartbeatInterval() {
        String logon = "8=FIX.4.4|9=57|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=001|";

        int work = process(logon);

        assertErrors("Missing field 108");
        assertWorkDone(work);
        assertNoOutMessages();
        assertSeqNums(2, 1);
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnLogonWithMismatchingHeartbeatInterval() {
        String logon = "8=FIX.4.4|9=64|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=11|10=056|";

        int work = process(logon);

        assertErrors("HeartBtInt(108) does not match, expected 30 but received 11");
        assertWorkDone(work);
        assertNoOutMessages();
        assertSeqNums(2, 1);
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnLogonWithResetSeqNumsAndSeqNumMoreOne() {
        String logon = "8=FIX.4.4|9=70|35=A|34=2|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|141=Y|10=100|";

        seqNums(101, 102);
        int work = process(logon);

        assertErrors("MsgSeqNum too high, expecting 1 but received 2");
        assertWorkDone(work);
        assertNoOutMessages();
        assertSeqNums(102, 101);
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnLogonWithResetSeqNumsAndWithoutHeartbeatInterval() {
        String logon = "8=FIX.4.4|9=63|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|141=Y|10=043|";

        seqNums(101, 102);
        int work = process(logon);

        assertErrors("Missing field 108");
        assertWorkDone(work);
        assertNoOutMessages();
        assertSeqNums(102, 101);
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnLogonWithSeqNumLessExpected() {
        String logon = "8=FIX.4.4|9=64|35=A|34=2|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=058|";

        seqNums(5, 5);
        int work = process(logon);

        assertErrors("MsgSeqNum too low, expecting 5 but received 2");
        assertWorkDone(work);
        assertNoOutMessages();
        assertSeqNums(5, 5);
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldSendResendRequestAfterLogonOnLogonWithSeqNumMoreExpected() {
        String inLogon = "8=FIX.4.4|9=64|35=A|34=5|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=061|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=0|10=071|";
        String outTestRequest = "8=FIX.4.4|9=77|35=1|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=MsgSeqNum check|10=062|";

        int work = process(inLogon);

        assertNoErrors();
        assertWorkDone(work);
        assertOutMessages(outLogon, outResendRequest, outTestRequest);
        assertSeqNums(1, 4);
        assertStatuses(SOCKET_CONNECTED, LOGON_RECEIVED, LOGON_SENT, APPLICATION_CONNECTED);
    }

    @Test
    public void shouldExchangeLogons() {
        String inLogon = "8=FIX.4.4|9=64|35=A|34=5|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=061|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=5|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=025|";
        String outTestRequest = "8=FIX.4.4|9=77|35=1|34=6|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=MsgSeqNum check|10=065|";

        seqNums(5, 5);
        int work = process(inLogon);

        assertNoErrors();
        assertWorkDone(work);
        assertOutMessages(outLogon, outTestRequest);
        assertSeqNums(6, 7);
        assertStatuses(SOCKET_CONNECTED, LOGON_RECEIVED, LOGON_SENT, APPLICATION_CONNECTED);
    }

    @Test
    public void shouldExchangeLogonsWithResetSeqNums() {
        String inLogon = "8=FIX.4.4|9=70|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|141=Y|10=099|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=Y|10=032|";
        String outTestRequest = "8=FIX.4.4|9=77|35=1|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=MsgSeqNum check|10=061|";

        seqNums(100, 100);
        int work = process(inLogon);

        assertNoErrors();
        assertWorkDone(work);
        assertOutMessages(outLogon, outTestRequest);
        assertSeqNums(2, 3);
        assertStatuses(SOCKET_CONNECTED, LOGON_RECEIVED, LOGON_SENT, APPLICATION_CONNECTED);
    }

}
