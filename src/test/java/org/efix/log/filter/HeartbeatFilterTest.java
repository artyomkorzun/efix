package org.efix.log.filter;

import org.junit.Test;


public class HeartbeatFilterTest extends FilterTest {

    public HeartbeatFilterTest() {
        super(new HeartbeatFilter());
    }

    @Test
    public void shouldPassMessages() {
        shouldPass("8=FIX.4.2|9=5|35=D|");
        shouldPass("8=FIX.4.2|9=5|35=A|");
        shouldPass("8=FIX.4.2|9=5|35=1|");
        shouldPass("8=FIX.4.2|9=5|35=2|");
        shouldPass("8=FIX.4.2|9=5|35=3|");
        shouldPass("8=FIX.4.2|9=5|35=4|");
        shouldPass("8=FIX.4.2|9=5|35=5|");
    }

    @Test
    public void shouldFilterHeartbeat() {
        shouldFilter("8=FIX.4.2|9=5|35=0|");
    }

}
