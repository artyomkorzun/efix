package org.f1x.state;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class SessionStateTest {

    protected final SessionState state;

    public SessionStateTest(SessionState state) {
        this.state = state;
    }

    @Before
    public void setUp() {
        state.open();
    }

    @After
    public void tearDown() {
        state.close();
    }

    @Test
    public void shouldSetAndGetFields() {
        assertEquals(SessionStatus.DISCONNECTED, state.getStatus());
        state.setStatus(SessionStatus.APPLICATION_CONNECTED);
        assertEquals(SessionStatus.APPLICATION_CONNECTED, state.getStatus());

        assertFalse(state.isTargetSeqNumSynchronized());
        state.setTargetSeqNumSynchronized(true);
        assertTrue(state.isTargetSeqNumSynchronized());

        assertEquals(1, state.getNextSenderSeqNum());
        state.setNextSenderSeqNum(99);
        assertEquals(99, state.getNextSenderSeqNum());

        assertEquals(1, state.getNextTargetSeqNum());
        state.setNextTargetSeqNum(999);
        assertEquals(999, state.getNextTargetSeqNum());

        assertEquals(Long.MIN_VALUE, state.getSessionStartTime());
        state.setSessionStartTime(9999);
        assertEquals(9999, state.getSessionStartTime());

        assertEquals(Long.MIN_VALUE, state.getLastSentTime());
        state.setLastSentTime(99999);
        assertEquals(99999, state.getLastSentTime());

        assertEquals(Long.MIN_VALUE, state.getLastReceivedTime());
        state.setLastReceivedTime(999999);
        assertEquals(999999, state.getLastReceivedTime());

        assertFalse(state.isTestRequestSent());
        state.setTestRequestSent(true);
        assertTrue(state.isTestRequestSent());
    }

    @Parameters(name = "{0}")
    public static Collection<SessionState> states() throws IOException {
        Path path = Files.createTempFile("Mapped-Session-State-", null);
        Files.delete(path);
        path.toFile().deleteOnExit();
        return Arrays.asList(new MemorySessionState(), new MappedSessionState(path));
    }

}
