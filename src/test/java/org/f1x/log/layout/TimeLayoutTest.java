package org.f1x.log.layout;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Test;

import static org.f1x.util.TestUtil.*;
import static org.junit.Assert.assertEquals;

public class TimeLayoutTest {

    protected static final long TIME = parseTimestamp("20160101-00:00:00");

    @Test
    public void shouldFormat() {
        TimeLayout layout = new TimeLayout();
        Buffer message = byteMessage("8=FIX.4.2|9=75|35=A|34=1|49=TTDEV14O|56=DELTIX|52=20160122-11:33:24.348|98=0|108=30|141=Y|10=045|");
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(1024);

        int size = layout.size(true, TIME, message, 0, message.capacity());
        layout.format(true, TIME, message, 0, message.capacity(), buffer, 0);

        String expected = "20160101-00:00:00.000: 8=FIX.4.2|9=75|35=A|34=1|49=TTDEV14O|56=DELTIX|52=20160122-11:33:24.348|98=0|108=30|141=Y|10=045|\n";
        String actual = BufferUtil.toString(buffer, 0, size);

        assertEquals("Fail format entry", stringMessage(expected), actual);
    }

}
