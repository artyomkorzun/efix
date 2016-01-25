package org.f1x.engine;

import org.f1x.connector.channel.TestChannel;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.TestUtil;
import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.concurrent.buffer.MessageHandler;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;

public class ReceiverTest {

    @Test
    public void shouldReceiveBrokenMessages() {
        String[] expected = {
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|",
                "8=FIX.4.2|9=77|35=1|34=2|49=TTDEV14O|56=DELTIX|52=20160122-11:33:24.349|112=MsgSeqNum check|10=075|",
                "8=FIX.4.2|9=77|35=0|34=2|49=DELTIX|52=20160122-11:33:24.350|56=TTDEV14O|112=MsgSeqNum check|10=066|",
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|"
        };

        String[] chunks = {
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|",
                "8=FIX.4.2|", "9=77|35=1|34=2|49=TTDEV14O|56=DELTIX|52=20", "160122-11:33:24.349|112=MsgSeqNum check|10=075|",
                "8=FIX.4.2|9=77|35=0|34=2|49=DELTIX|52=20160122-11:33:24.350|56=TTDEV14O|112=MsgSeqNum check|", "10=066|" +
                "8=FIX.4.2|9=87|35=A|34=1|49=DELTIX|52=20160122-11:33:24.322|", "56=TTDEV14O|95=3|96=ffd|98=0|108=30|141=Y|10=228|"
        };

        shouldReceiveMessages(expected, chunks);
    }

    @Test(expected = InsufficientSpaceException.class)
    public void shouldThrowExceptionMessageLengthExceedsMax() {
        Receiver receiver = new Receiver(1024);
        receiver.setChannel(new TestChannel("8=FIX.4.2|9=1024|---------------------------------------------------------------------------------->"));
        receiver.receive(null);
    }

    protected void shouldReceiveMessages(String[] expected, String[] chunks) {
        for (int i = 0; i < expected.length; i++)
            expected[i] = TestUtil.normalize(expected[i]);

        Receiver receiver = new Receiver(1024);
        receiver.setChannel(new TestChannel(chunks));

        ArrayList<String> messages = new ArrayList<>();
        MessageHandler verifier = (messageType, buffer, offset, length) -> messages.add(BufferUtil.toString(buffer, offset, length));
        while (receiver.receive(verifier) > 0) ;

        assertArrayEquals(expected, messages.toArray());
    }

}
