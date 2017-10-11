package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.SessionType;
import org.efix.connector.channel.TestConnector;
import org.efix.connector.channel.TextChannel;
import org.efix.message.Header;
import org.efix.message.Message;
import org.efix.state.MemorySessionState;
import org.efix.state.SessionState;
import org.efix.state.SessionStatus;
import org.efix.store.MemoryMessageStore;
import org.efix.store.MessageStore;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.EpochClock;
import org.efix.util.HaltedEpochClock;
import org.efix.util.buffer.Buffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static org.efix.state.SessionStatus.*;
import static org.efix.util.TestUtil.*;
import static org.junit.Assert.*;


public abstract class SessionTest {

    protected static final SessionId SESSION_ID = new SessionId("RECEIVER", "SENDER");
    protected static final EpochClock CLOCK = new HaltedEpochClock(parseTimestampMs("20160101-00:00:00"));

    protected final SessionType sessionType;

    protected final List<String> errors = new ArrayList<>();
    protected final List<SessionStatus> statuses = new ArrayList<>();

    protected final TextChannel channel = new TextChannel();

    protected Session session;

    protected SessionState state;
    protected MessageStore store;

    public SessionTest(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    @Before
    public void setUp() {
        state = new MemorySessionState();
        store = new MemoryMessageStore(1 << 16);

        SessionContext context = new SessionContext(new InetSocketAddress(1234), sessionType, FixVersion.FIX44, SESSION_ID);
        context.clock(CLOCK).state(state).store(store).connector(new TestConnector(channel)).syncBatchSize(100);

        session = new Session(context) {
            @Override
            protected int doSendMessages() {
                return 0;
            }

            @Override
            protected void onAdminMessage(Header header, Message message) {
            }

            @Override
            protected void onAppMessage(Header header, Message message) {
            }

            @Override
            protected void onError(Exception e) {
                errors.add(e.getMessage());
            }

            @Override
            protected void onStatusUpdate(SessionStatus previous, SessionStatus current) {
                statuses.add(current);
            }

        };
    }

    @After
    public void tearDown() {
        errors.clear();
        statuses.clear();
        channel.clear();
    }

    // -------------------- LOGOUT --------------------

    @Test
    public void shouldProcessLogout() {
        String inLogout = "8=FIX.4.4|9=57|35=5|34=3|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=247|";
        String outLogout = "8=FIX.4.4|9=76|35=5|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|58=Logout response|10=132|";

        exchangeLogons();

        int work = process(inLogout);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 3);
        assertOutMessages(outLogout);
        assertStatuses(LOGOUT_RECEIVED, LOGOUT_SENT, SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldExchangeLogouts() {
        String inLogout = "8=FIX.4.4|9=57|35=5|34=3|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=247|";
        String outLogout = "8=FIX.4.4|9=72|35=5|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|58=Session end|10=178|";

        exchangeLogons();

        session.sendLogout("Session end");
        int work = process(inLogout);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 3);
        assertOutMessages(outLogout);
        assertStatuses(LOGOUT_SENT, LOGOUT_RECEIVED, SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnLogoutWithSeqNumMoreExpected() {
        String inLogout = "8=FIX.4.4|9=57|35=5|34=4|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=248|";
        String outLogout = "8=FIX.4.4|9=76|35=5|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|58=Logout response|10=132|";
        exchangeLogons();

        int work = process(inLogout);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outLogout);
        assertStatuses(LOGOUT_RECEIVED, LOGOUT_SENT, SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldSendLogoutOnLogoutWithSeqNumLessExpected() {
        String inLogout = "8=FIX.4.4|9=57|35=5|34=2|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=246|";

        exchangeLogons();

        int work = process(inLogout);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldResendMessagesWhileLogout() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=1|16=0|10=080|";
        String inLogout = "8=FIX.4.4|9=57|35=5|34=4|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|10=248|";

        String outLogout = "8=FIX.4.4|9=72|35=5|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|58=Session end|10=178|";
        String outGapFill = "8=FIX.4.4|9=99|35=4|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|36=3|123=Y|10=181|";

        exchangeLogons();
        session.sendLogout("Session end");

        int work = process(inResendRequest, inLogout);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(5, 3);
        assertOutMessages(outLogout, outGapFill);
        assertStatuses(LOGOUT_SENT, LOGOUT_RECEIVED, SOCKET_CONNECTED, DISCONNECTED);
    }

    // -------------------- HEARTBEAT --------------------

    @Test
    public void shouldSendResendRequestOnHeartbeatWithSeqNumMoreExpected() {
        String inHeartbeat = "8=FIX.4.4|9=57|35=0|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=212|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=077|";

        exchangeLogons();

        int work = process(inHeartbeat);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnHeartbeatWithSeqNumLessExpected() {
        String inHeartbeat = "8=FIX.4.4|9=57|35=0|34=2|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=210|";

        exchangeLogons();

        int work = process(inHeartbeat);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    // -------------------- TEST REQUEST --------------------

    @Test
    public void shouldSendHeartbeatOnTestRequest() {
        String inTestRequest = "8=FIX.4.4|9=77|35=1|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|112=Test Request ID|10=254|";
        String outHeartbeat = "8=FIX.4.4|9=77|35=0|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=Test Request ID|10=244|";

        exchangeLogons();

        int work = process(inTestRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 3);
        assertOutMessages(outHeartbeat);
        assertStatuses();
    }

    @Test
    public void shouldSendHeartbeatOnTestRequestWithSeqNumMoreExpectedEvenIfSeqNumIsNotSynced() {
        String inTestRequest = "8=FIX.4.4|9=77|35=1|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|112=Test Request ID|10=255|";
        String outHeartbeat = "8=FIX.4.4|9=77|35=0|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=Test Request ID|10=244|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=078|";

        exchangeLogons();
        int work = process(inTestRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 4);
        assertOutMessages(outHeartbeat, outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldSendResendRequestOnTestRequestWithSeqNumMoreExpected() {
        String inTestRequest = "8=FIX.4.4|9=77|35=1|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|112=Test Request ID|10=255|";
        String outHeartbeat = "8=FIX.4.4|9=77|35=0|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|112=Test Request ID|10=244|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=078|";

        exchangeLogons();

        int work = process(inTestRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 4);
        assertOutMessages(outHeartbeat, outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnTestRequestWithSeqNumLessExpected() {
        String inTestRequest = "8=FIX.4.4|9=77|35=1|34=2|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|112=Test Request ID|10=253|";

        exchangeLogons();

        int work = process(inTestRequest);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnTestRequestWithoutReqID() {
        String inTestRequest = "8=FIX.4.4|9=57|35=1|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=212|";

        exchangeLogons();

        int work = process(inTestRequest);

        assertErrors("Field #112 not found");
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    // -------------------- SEQUENCE RESET --------------------

    @Test
    public void shouldDisconnectOnGapFillWithSeqNumLessExpected() {
        String inGapFill = "8=FIX.4.4|9=69|35=4|34=1|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=10|123=Y|10=013|";

        exchangeLogons();

        int work = process(inGapFill);

        assertErrors("MsgSeqNum too low, expecting 3 but received 1");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldSendResendRequestOnGapFillWithSeqNumMoreExpected() {
        String inGapFill = "8=FIX.4.4|9=69|35=4|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=10|123=Y|10=016|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=077|";

        exchangeLogons();

        int work = process(inGapFill);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnGapFillWithoutNewSeqNum() {
        String inGapFill = "8=FIX.4.4|9=63|35=4|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|123=Y|10=001|";

        exchangeLogons();

        int work = process(inGapFill);

        assertErrors("Field #36 not found");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnGapFillWithNewSeqNumEqualExpected() {
        String inGapFill = "8=FIX.4.4|9=68|35=4|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=3|123=Y|10=224|";

        exchangeLogons();

        int work = process(inGapFill);

        assertErrors("NewSeqNo(36) 3 should be more expected target MsgSeqNum 3");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldSetNewTargetSeqNumOnGapFill() {
        String inGapFill = "8=FIX.4.4|9=70|35=4|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=100|123=Y|10=055|";

        exchangeLogons();

        int work = process(inGapFill);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(100, 2);
        assertNoOutMessages();
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnSequenceResetWithNewSeqNumEqualExpected() {
        String inSequenceReset = "8=FIX.4.4|9=62|35=4|34=1|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=3|10=171|";

        exchangeLogons();

        int work = process(inSequenceReset);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses();
    }

    @Test
    public void shouldSetNewTargetSeqNumOnSequenceResetWithSeqNumLessExpected() {
        String inSequenceReset = "8=FIX.4.4|9=70|35=4|34=1|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|36=100|123=N|10=042|";

        exchangeLogons();

        int work = process(inSequenceReset);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(100, 2);
        assertNoOutMessages();
        assertStatuses();
    }

    // -------------------- REJECT --------------------

    @Test
    public void shouldSendResendRequestOnRejectWithSeqNumMoreExpected() {
        String inReject = "8=FIX.4.4|9=57|35=3|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=215|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=077|";

        exchangeLogons();

        int work = process(inReject);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnRejectWithSeqNumLessExpected() {
        String inHeartbeat = "8=FIX.4.4|9=57|35=3|34=2|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=213|";

        exchangeLogons();

        int work = process(inHeartbeat);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldProcessReject() {
        String inHeartbeat = "8=FIX.4.4|9=57|35=3|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=214|";

        exchangeLogons();

        int work = process(inHeartbeat);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses();
    }

    // -------------------- RESEND REQUEST --------------------

    @Test
    public void shouldResendMessageOnResendRequest() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=2|16=2|10=083|";

        String outOrderSingle = "8=FIX.4.4|9=67|35=D|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|1=ACCOUNT|10=091|";
        String outResendOrderSingle = "8=FIX.4.4|9=98|35=D|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";

        exchangeLogons();
        sendAppMessage("D", "1=ACCOUNT|");

        int work = process(inResendRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 3);
        assertOutMessages(outOrderSingle, outResendOrderSingle);
        assertStatuses();
    }

    @Test
    public void shouldResendGapsAndMessagesOnResendRequestWithZeroEndSeqNo() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=1|16=0|10=080|";

        String outOrderSingle = "8=FIX.4.4|9=67|35=D|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|1=ACCOUNT|10=091|";
        String outFirstHeartbeat = "8=FIX.4.4|9=57|35=0|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|10=203|";
        String outExecutionReport = "8=FIX.4.4|9=70|35=8|34=4|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|100=EXCHANGE|10=225|";
        String outSecondHeartbeat = "8=FIX.4.4|9=57|35=0|34=5|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|10=205|";

        String firstGapFill = "8=FIX.4.4|9=99|35=4|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|36=2|123=Y|10=180|";
        String outResendOrderSingle = "8=FIX.4.4|9=98|35=D|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";
        String secondGapFill = "8=FIX.4.4|9=99|35=4|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|36=4|123=Y|10=184|";
        String outResendExecutionReport = "8=FIX.4.4|9=101|35=8|34=4|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|100=EXCHANGE|10=231|";
        String thirdGapFill = "8=FIX.4.4|9=99|35=4|34=5|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|36=6|123=Y|10=188|";

        exchangeLogons();
        sendAppMessage("D", "1=ACCOUNT|");
        session.sendHeartbeat(null);
        sendAppMessage("8", "100=EXCHANGE|");
        session.sendHeartbeat(null);

        int work = process(inResendRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 6);

        assertOutMessages(
                outOrderSingle, outFirstHeartbeat, outExecutionReport, outSecondHeartbeat,
                firstGapFill, outResendOrderSingle, secondGapFill, outResendExecutionReport, thirdGapFill
        );

        assertStatuses();
    }

    @Test
    public void shouldSyncWithLastGapFill() {
        String inLogon = "8=FIX.4.4|9=66|35=A|34=205|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=057|";
        String inGapFill1 = "8=FIX.4.4|9=69|35=4|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=50|123=Y|10=057|";
        String inGapFill2 = "8=FIX.4.4|9=71|35=4|34=50|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=101|123=Y|10=057|";
        String inGapFill3 = "8=FIX.4.4|9=72|35=4|34=101|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=201|123=Y|10=057|";
        String inMessage = "8=FIX.4.4|9=100|35=D|34=201|49=SENDER|56=RECEIVER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";
        String inGapFill4 = "8=FIX.4.4|9=72|35=4|34=202|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=206|123=Y|10=057|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outResendRequest1 = "8=FIX.4.4|9=68|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=100|10=170|";
        String outResendRequest2 = "8=FIX.4.4|9=70|35=2|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=101|16=200|10=006|";
        String outResendRequest3 = "8=FIX.4.4|9=70|35=2|34=4|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=201|16=205|10=013|";

        int work = process(inLogon, inGapFill1, inGapFill2, inGapFill3, inMessage, inGapFill4);

        assertNoErrors();
        assertWorkDone(work);
        assertStatus(SessionStatus.APPLICATION_CONNECTED);
        assertSeqNums(206, 5);
        assertFalse(state.testRequestSent());
        assertOutMessages(outLogon, outResendRequest1, outResendRequest2, outResendRequest3);
    }

    @Test
    public void shouldSyncWithLastAppMessage() {
        String inLogon = "8=FIX.4.4|9=66|35=A|34=200|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=057|";
        String inGapFill1 = "8=FIX.4.4|9=69|35=4|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=50|123=Y|10=057|";
        String inGapFill2 = "8=FIX.4.4|9=71|35=4|34=50|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=101|123=Y|10=057|";
        String inMessage1 = "8=FIX.4.4|9=69|35=D|34=201|49=SENDER|56=RECEIVER|52=20160101-00:00:00.000|1=ACCOUNT|10=058|";
        String inGapFill3 = "8=FIX.4.4|9=72|35=4|34=101|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=201|123=Y|10=057|";
        String inMessage2 = "8=FIX.4.4|9=100|35=D|34=201|49=SENDER|56=RECEIVER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outResendRequest1 = "8=FIX.4.4|9=68|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=100|10=170|";
        String outResendRequest2 = "8=FIX.4.4|9=70|35=2|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=101|16=200|10=006|";
        String outResendRequest3 = "8=FIX.4.4|9=70|35=2|34=4|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=201|16=201|10=009|";

        int work = process(inLogon, inGapFill1, inGapFill2, inMessage1, inGapFill3, inMessage2);

        assertNoErrors();
        assertWorkDone(work);
        assertStatus(SessionStatus.APPLICATION_CONNECTED);
        assertSeqNums(202, 5);
        assertFalse(state.testRequestSent());
        assertOutMessages(outLogon, outResendRequest1, outResendRequest2, outResendRequest3);
    }

    @Test
    public void shouldSyncWithTwoGaps() {
        String inLogon = "8=FIX.4.4|9=66|35=A|34=200|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=057|";
        String inGapFill1 = "8=FIX.4.4|9=70|35=4|34=2|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=100|123=Y|10=057|";
        String inGapFill2 = "8=FIX.4.4|9=70|35=4|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|36=101|123=Y|10=057|";
        String inMessage1 = "8=FIX.4.4|9=100|35=D|34=101|49=SENDER|56=RECEIVER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";
        String inMessage2 = "8=FIX.4.4|9=100|35=D|34=102|49=SENDER|56=RECEIVER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|1=ACCOUNT|10=058|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";
        String outResendRequest1 = "8=FIX.4.4|9=68|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=100|10=170|";
        String outResendRequest2 = "8=FIX.4.4|9=68|35=2|34=3|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=1|16=100|10=171|";
        String outResendRequest3 = "8=FIX.4.4|9=70|35=2|34=4|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=101|16=200|10=007|";
        String outResendRequest4 = "8=FIX.4.4|9=70|35=2|34=5|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=101|16=200|10=008|";

        int work = process(inLogon, inGapFill1, inGapFill2, inMessage2, inMessage1, inMessage2);

        assertNoErrors();
        assertWorkDone(work);
        assertStatus(SessionStatus.APPLICATION_CONNECTED);
        assertSeqNums(103, 6);
        assertFalse(state.testRequestSent());
        assertOutMessages(outLogon, outResendRequest1, outResendRequest2, outResendRequest3, outResendRequest4);
    }

    @Test
    public void shouldDisconnectOnResendRequestWithSeqNumLessExpected() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=1|16=0|10=079|";

        exchangeLogons();

        int work = process(inResendRequest);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldSendGapFillAndResendRequestOnResendRequestWithSeqNumMoreExpected() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=1|16=0|10=081|";

        String outGapFill = "8=FIX.4.4|9=99|35=4|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|122=20160101-00:00:00.000|43=Y|36=2|123=Y|10=180|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=077|";

        exchangeLogons();

        int work = process(inResendRequest);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outGapFill, outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnResendRequestWithoutBeginSeqNo() {
        String inResendRequest = "8=FIX.4.4|9=62|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|16=0|10=166|";

        exchangeLogons();

        int work = process(inResendRequest);

        assertErrors("Field #7 not found");
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnResendRequestWithoutEndSeqNo() {
        String inResendRequest = "8=FIX.4.4|9=61|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=1|10=118|";

        exchangeLogons();

        int work = process(inResendRequest);

        assertErrors("Field #16 not found");
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldDisconnectOnResendRequestWithBeginSeqNoMoreEndSeqNo() {
        String inResendRequest = "8=FIX.4.4|9=66|35=2|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|7=5|16=4|10=088|";

        exchangeLogons();

        int work = process(inResendRequest);

        assertErrors("BeginSeqNo(7) 5 more EndSeqNo(16) 4");
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    // -------------------- APPLICATION MESSAGE --------------------

    @Test
    public void shouldSendResendRequestOnAppMessageWithSeqNumMoreExpected() {
        String appMessage = "8=FIX.4.4|9=57|35=D|34=4|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=232|";
        String outResendRequest = "8=FIX.4.4|9=66|35=2|34=2|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|7=3|16=4|10=077|";

        exchangeLogons();

        int work = process(appMessage);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertOutMessages(outResendRequest);
        assertStatuses();
    }

    @Test
    public void shouldDisconnectOnAppMessageWithSeqNumLessExpected() {
        String appMessage = "8=FIX.4.4|9=57|35=D|34=2|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=230|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("MsgSeqNum too low, expecting 3 but received 2");
        assertWorkDone(work);
        assertSeqNums(3, 2);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }

    @Test
    public void shouldProcessAppMessage() {
        String appMessage = "8=FIX.4.4|9=57|35=D|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=231|";

        exchangeLogons();

        int work = process(appMessage);

        assertNoErrors();
        assertWorkDone(work);
        assertSeqNums(4, 2);
        assertNoOutMessages();
        assertStatuses();
    }

    /*@Test
    public void shouldDisconnectOnMessageWithWrongBeginString() {
        String appMessage = "8=FIX.4.2|9=57|35=D|34=3|49=SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=136|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("Field #8 does not match, expected FIX.4.4 but received FIX.4.2");
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }*/

   /* @Test
    public void shouldDisconnectOnMessageWithWrongSenderCompId() {
        String appMessage = "8=FIX.4.4|9=63|35=D|34=3|49=WRONG SENDER|52=19700101-00:00:00.000|56=RECEIVER|10=136|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("Field #49 does not match, expected SENDER but received WRONG SENDER");
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }*/

    /*@Test
    public void shouldDisconnectOnMessageWithWrongTargetCompId() {
        String appMessage = "8=FIX.4.4|9=63|35=D|34=3|49=SENDER|52=19700101-00:00:00.000|56=WRONG RECEIVER|10=136|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("Field #56 does not match, expected RECEIVER but received WRONG RECEIVER");
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }*/

   /* @Test
    public void shouldDisconnectOnAppMessageWithoutSendingTime() {
        String appMessage = "8=FIX.4.4|9=63|35=D|34=3|49=SENDER|56=WRONG RECEIVER|1=AAAAAAAAAAAAAAAAAAAAAA|10=136|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("Missing field #52");
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }*/

    /*@Test
    public void shouldDisconnectOnDuplicatedAppMessageWithoutOriginalSendingTime() {
        String appMessage = "8=FIX.4.4|9=93|35=D|34=3|49=SENDER|56=WRONG RECEIVER|52=19700101-00:00:00.000|43=Y|1=AAAAAAAAAAAAAAAAAAAAAA|10=136|";

        exchangeLogons();

        int work = process(appMessage);

        assertErrors("Missing field #122");
        assertWorkDone(work);
        assertSeqNums(3, 3);
        assertNoOutMessages();
        assertStatuses(SOCKET_CONNECTED, DISCONNECTED);
    }*/

    protected void seqNums(int senderSeqNum, int targetSeqNum) {
        state.senderSeqNum(senderSeqNum);
        state.targetSeqNum(targetSeqNum);
    }

    protected void exchangeLogons() {
        String inLogon = "8=FIX.4.4|9=64|35=A|34=1|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|108=30|10=057|";
        String inHeartbeat = "8=FIX.4.4|9=77|35=0|34=2|49=SENDER|52=20140522-12:07:39.552|56=RECEIVER|112=MsgSeqNum check|10=099|";

        String outLogon = "8=FIX.4.4|9=75|35=A|34=1|49=RECEIVER|56=SENDER|52=20160101-00:00:00.000|98=0|108=30|141=N|10=021|";

        int work = process(inLogon, inHeartbeat);

        assertNoErrors();
        assertWorkDone(work);
        assertStatus(SessionStatus.APPLICATION_CONNECTED);
        assertSeqNums(3, 2);
        assertFalse(state.testRequestSent());
        assertOutMessages(outLogon);

        statuses.clear();
        channel.clear();
    }

    protected void sendAppMessage(String msgType, String message) {
        Buffer buffer = byteMessage(message);
        session.sendMessage(ByteSequenceWrapper.of(msgType), buffer, 0, buffer.capacity());
    }

    protected int process(String... inMessages) {
        int work = 0;
        for (String inMessage : inMessages) {
            channel.inQueue().add(inMessage);
            work += session.work();
        }

        return work;
    }

    protected void assertWorkDone(int work) {
        assertTrue("No work done", work > 0);
    }

    protected void assertOutMessages(String... expected) {
        Queue<String> messages = channel.outQueue();
        assertArrayEquals("Outbound messages don't match", stringMessages(expected), messages.toArray());
    }

    protected void assertNoOutMessages() {
        Queue<String> messages = channel.outQueue();
        assertEquals("No outbound messages expected", 0, messages.size());
    }

    protected void assertSeqNums(int target, int sender) {
        assertEquals("Sender sequence number", sender, state.senderSeqNum());
        assertEquals("Target sequence number", target, state.targetSeqNum());
    }

    protected void assertErrors(String... expected) {
        StringBuilder expect = new StringBuilder();
        Arrays.asList(expected).forEach(e -> expect.append(e).append("\n"));

        StringBuilder actual = new StringBuilder();
        errors.forEach(e -> actual.append(e).append("\n"));

        assertEquals("Errors don't match", expect.toString(), actual.toString());
    }

    protected void assertNoErrors() {
        assertErrors();
    }

    protected void assertStatuses(SessionStatus... expected) {
        assertArrayEquals("Statuses don't match", expected, statuses.toArray());
    }

    protected void assertStatus(SessionStatus status) {
        assertEquals(status, state.status());
    }

}
