package org.efix.session;

import org.efix.SessionType;
import org.junit.Test;

import static org.efix.state.SessionStatus.*;

public class SessionInitiatorTest extends SessionTest {

    public SessionInitiatorTest() {
        super(SessionType.INITIATOR);
    }

    // -------------------- LOGON --------------------

    @Test
    public void shouldExchangeLogons() {
        String inLogon = "8=FIX.4.4|9=64|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=057|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outTestRequest = "8=FIX.4.4|9=77|35=1|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=MsgSeqNum check|10=061|";

        int work = process(inLogon);

        assertNoErrors();
        assertWorkDone(work);
        assertOutMessages(outLogon, outTestRequest);
        assertSeqNums(2, 3);
        assertStatuses(SOCKET_CONNECTED, LOGON_SENT, LOGON_RECEIVED, APPLICATION_CONNECTED);
    }

    @Test
    public void shouldSendResendRequestOnLogonWithSeqNumMoreExpected() {
        String inLogon = "8=FIX.4.4|9=64|35=A|34=5|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=061|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=0|10=071|";
        String outTestRequest = "8=FIX.4.4|9=77|35=1|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=MsgSeqNum check|10=062|";

        int work = process(inLogon);

        assertNoErrors();
        assertWorkDone(work);
        assertOutMessages(outLogon, outResendRequest, outTestRequest);
        assertSeqNums(1, 4);
        assertStatuses(SOCKET_CONNECTED, LOGON_SENT, LOGON_RECEIVED, APPLICATION_CONNECTED);
    }

}
