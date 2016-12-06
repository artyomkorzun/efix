package org.efix.log.filter;

import org.efix.util.TestUtil;
import org.efix.util.buffer.UnsafeBuffer;
import org.junit.Assert;


public abstract class FilterTest {

    protected final Filter filter;

    public FilterTest(Filter filter) {
        this.filter = filter;
    }

    public void shouldPass(String message) {
        UnsafeBuffer buffer = TestUtil.byteMessage(message);

        boolean accept = filter.filter(true, 0, buffer, 0, buffer.capacity());
        Assert.assertFalse(message, accept);

        accept = filter.filter(false, -1, buffer, 0, buffer.capacity());
        Assert.assertFalse(message, accept);
    }

    public void shouldFilter(String message) {
        UnsafeBuffer buffer = TestUtil.byteMessage(message);

        boolean accept = filter.filter(true, 0, buffer, 0, buffer.capacity());
        Assert.assertTrue(message, accept);

        accept = filter.filter(false, -1, buffer, 0, buffer.capacity());
        Assert.assertTrue(message, accept);
    }

}
