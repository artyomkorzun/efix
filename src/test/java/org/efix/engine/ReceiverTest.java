package org.efix.engine;

import org.efix.connector.channel.TextChannel;
import org.efix.message.FieldException;
import org.efix.util.InsufficientSpaceException;
import org.efix.util.buffer.BufferUtil;
import org.junit.Test;

import java.util.ArrayList;

import static org.efix.util.TestUtil.stringMessages;
import static org.junit.Assert.assertArrayEquals;

public class ReceiverTest {

    protected static final int BUFFER_SIZE = 1024;
    protected static final int MTU_SIZE = BUFFER_SIZE;

    @Test
    public void shouldReceiveBrokenMessages() {
        String[] expected = {
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|",
                "8=FIX.4.2|9=77|35=1|34=2|49=TTDEV14O|56=DELTIX|52=20160122-11:33:24.349|112=MsgSeqNum check|10=075|",
                "8=FIX.4.2|9=77|35=0|34=2|49=DELTIX|52=20160122-11:33:24.350|56=TTDEV14O|112=MsgSeqNum check|10=066|",
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|"
        };

        String[] chunks = {
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|", "",
                "8=FIX.4.2|", "9=77|35=1|34=2|49=TTDEV14O|56=DELTIX|52=20", "160122-11:33:24.349|112=MsgSeqNum check|10=075|",
                "8=FIX.4.2|9=77|35=0|34=2|49=DELTIX|52=20160122-11:33:24.350|56=TTDEV14O|112=MsgSeqNum check|", "10=066|", "",
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|", "56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|",
        };

        shouldReceiveMessages(expected, chunks);
    }

    @Test(expected = InsufficientSpaceException.class)
    public void shouldThrowExceptionMessageLengthExceedsMax() {
        shouldThrowException("8=FIX.4.2|9=1024|---------------------------------------------------------------------------------->");
    }

    @Test(expected = FieldException.class)
    public void shouldThrowExceptionBodyLengthNonPositive() {
        shouldThrowException("8=FIX.4.2|9=0|---------------------------------------------------------------------------------->");
    }

    @Test(expected = FieldException.class)
    public void shouldThrowExceptionMissingBeginString() {
        shouldThrowException("9=150|---------------------------------------------------------------------------------->");
    }

    @Test(expected = FieldException.class)
    public void shouldThrowExceptionMissingBodyLength() {
        shouldThrowException("8=FIX.4.2|19=150|---------------------------------------------------------------------------------->");
    }

    protected void shouldReceiveMessages(String[] expected, String[] chunks) {
        Receiver receiver = new Receiver(BUFFER_SIZE, MTU_SIZE);
        TextChannel channel = new TextChannel(chunks);
        receiver.channel(channel);

        ArrayList<String> messages = new ArrayList<>();
        MessageHandler verifier = (buffer, offset, length) -> messages.add(BufferUtil.toString(buffer, offset, length));
        while (!channel.inQueue().isEmpty())
            receiver.receive(verifier);

        assertArrayEquals(stringMessages(expected), messages.toArray());
    }

    protected void shouldThrowException(String... chunks) {
        Receiver receiver = new Receiver(BUFFER_SIZE, MTU_SIZE);
        receiver.channel(new TextChannel(chunks));
        MessageHandler handler = (buffer, offset, length) -> {
        };

        while (receiver.receive(handler) > 0) ;
    }

}
