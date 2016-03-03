package org.efix.util.concurrent.queue;

import org.efix.util.MutableInt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class QueueTest {

    protected static final int CAPACITY = 1024;

    private final Queue<Integer> queue;

    public QueueTest(Queue<Integer> queue) {
        this.queue = queue;
    }

    @Test
    public void shouldOfferAndPollObjects() {
        assertEquals(CAPACITY, queue.capacity());
        assertTrue(queue.isEmpty());

        assertTrue(queue.offer(1));
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());

        assertEquals(1, queue.poll());
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        for (int i = 1; i <= CAPACITY; i++) {
            assertTrue(queue.offer(i));
            assertFalse(queue.isEmpty());
            assertEquals(i, queue.size());
        }

        assertFalse(queue.offer(1));
        assertFalse(queue.isEmpty());
        assertEquals(CAPACITY, queue.size());

        for (int i = 1; i <= CAPACITY; i++) {
            assertFalse(queue.isEmpty());
            assertEquals(i, queue.poll());
            assertEquals(CAPACITY - i, queue.size());
        }

        assertTrue(queue.isEmpty());
        assertNull(queue.poll());
    }

    @Test
    public void shouldDrainObjects() {
        for (int objectsAdded = 1; objectsAdded <= CAPACITY; objectsAdded++) {
            for (int i = 1; i <= objectsAdded; i++)
                assertTrue(queue.offer(i));

            MutableInt expected = new MutableInt(0);
            int objectsRead = queue.drain(number -> assertEquals(increment(expected), number));
            assertEquals(objectsAdded, objectsRead);
        }
    }

    protected int increment(MutableInt number) {
        number.set(number.get() + 1);
        return number.get();
    }

    @Parameters(name = "{0}")
    public static Collection<Queue<Integer>> queues() {
        return Arrays.asList(new SPSCQueue<>(CAPACITY), new MPSCQueue<>(CAPACITY));
    }

    protected static void assertEquals(int expected, int actual) {
        Assert.assertEquals(expected, actual);
    }


}
