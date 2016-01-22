package org.f1x.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AtomicLongTest {

    @Test
    public void shouldSetAndGetNumbers() {
        AtomicLong atomicLong = new AtomicLong(5);
        assertEquals(5, atomicLong);

        atomicLong.set(15);
        assertEquals(15, atomicLong);

        atomicLong.setOrdered(25);
        assertEquals(25, atomicLong);

        atomicLong.setVolatile(35);
        assertEquals(35, atomicLong);

        assertFalse(atomicLong.compareAndSet(0, 45));
        assertEquals(35, atomicLong);

        assertTrue(atomicLong.compareAndSet(35, 45));
        assertEquals(45, atomicLong);
    }

    protected static void assertEquals(long expected, AtomicLong atomicLong) {
        Assert.assertEquals(expected, atomicLong.get());
        Assert.assertEquals(expected, atomicLong.getVolatile());
    }

}
