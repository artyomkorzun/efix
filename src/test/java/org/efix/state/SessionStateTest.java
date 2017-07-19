package org.efix.state;

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
        assertEquals(SessionStatus.DISCONNECTED, state.status());
        state.status(SessionStatus.APPLICATION_CONNECTED);
        assertEquals(SessionStatus.APPLICATION_CONNECTED, state.status());

        assertEquals(1, state.senderSeqNum());
        state.senderSeqNum(99);
        assertEquals(99, state.senderSeqNum());

        assertEquals(1, state.targetSeqNum());
        state.targetSeqNum(999);
        assertEquals(999, state.targetSeqNum());

        assertEquals(Long.MIN_VALUE, state.sessionStartTime());
        state.sessionStartTime(9999);
        assertEquals(9999, state.sessionStartTime());

        assertEquals(Long.MIN_VALUE, state.lastSentTime());
        state.lastSentTime(99999);
        assertEquals(99999, state.lastSentTime());

        assertEquals(Long.MIN_VALUE, state.lastReceivedTime());
        state.lastReceivedTime(999999);
        assertEquals(999999, state.lastReceivedTime());

        assertFalse(state.testRequestSent());
        state.testRequestSent(true);
        assertTrue(state.testRequestSent());
    }

    @Parameters(name = "{0}")
    public static Collection<SessionState> states() throws IOException {
        Path path = Files.createTempFile("Mapped-Session-State-", null);
        Files.delete(path);
        path.toFile().deleteOnExit();
        return Arrays.asList(new MemorySessionState(), new MappedSessionState(path));
    }

}
