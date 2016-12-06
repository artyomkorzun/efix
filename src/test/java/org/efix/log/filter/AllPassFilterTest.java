package org.efix.log.filter;

import org.junit.Test;


public class AllPassFilterTest extends FilterTest {

    public AllPassFilterTest() {
        super(Filter.ALL_PASS);
    }

    @Test
    public void shouldPassMessages() {
        shouldPass("8=FIX.4.2|9=5|35=D|");
        shouldPass("8=FIX.4.2|9=5|35=A|");
        shouldPass("8=FIX.4.2|9=5|35=0|");
        shouldPass("8=FIX.4.2|9=5|35=1|");
        shouldPass("8=FIX.4.2|9=5|35=2|");
        shouldPass("8=FIX.4.2|9=5|35=3|");
        shouldPass("8=FIX.4.2|9=5|35=4|");
        shouldPass("8=FIX.4.2|9=5|35=5|");
    }

}
