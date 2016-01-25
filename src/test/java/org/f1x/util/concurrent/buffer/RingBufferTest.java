package org.f1x.util.concurrent.buffer;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.f1x.util.BitUtil.align;
import static org.f1x.util.concurrent.buffer.AbstractRingBuffer.ALIGNMENT;
import static org.f1x.util.concurrent.buffer.AbstractRingBuffer.HEADER_LENGTH;
import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class RingBufferTest {

    protected static final int MSG_TYPE = 3;
    protected static final int CAPACITY = 1 << 12;

    protected final AbstractRingBuffer buffer;

    public RingBufferTest(AbstractRingBuffer buffer) {
        this.buffer = buffer;
    }

    @Before
    public void setUp() {
        buffer.headSequence.set(0);
        buffer.tailSequence.set(0);
        buffer.tailCacheSequence.set(0);
    }

    @Test
    public void shouldWriteMessages() {
        int messageCount = 16;
        int messageLength = CAPACITY / messageCount - HEADER_LENGTH;
        MutableBuffer message = UnsafeBuffer.allocateHeap(messageLength);
        for (int i = 0, size = 0; i < messageCount; i++) {
            size += messageLength + HEADER_LENGTH;

            assertTrue(buffer.write(MSG_TYPE, message, 0, messageLength));
            assertFalse(buffer.isEmpty());
            assertEquals(size, buffer.size());
        }
    }

    @Test
    public void shouldInsertPaddingAndWriteMessage() {
        int padding = 256;
        buffer.tailSequence.set(CAPACITY - padding);
        buffer.headSequence.set(CAPACITY - padding);

        int messageLength = 512;
        MutableBuffer message = UnsafeBuffer.allocateHeap(messageLength);
        assertTrue(buffer.write(MSG_TYPE, message, 0, messageLength));

        int size = padding + messageLength + HEADER_LENGTH;
        assertEquals(size, buffer.size());
    }

    @Test
    public void shouldNotWriteMessageToFullBuffer() {
        buffer.headSequence.set(CAPACITY);

        int messageLength = 16;
        MutableBuffer message = UnsafeBuffer.allocateHeap(messageLength);
        assertFalse(buffer.write(MSG_TYPE, message, 0, messageLength));
        assertEquals(CAPACITY, buffer.size());
        assertFalse(buffer.isEmpty());
    }

    @Test
    public void shouldNotWriteMessageToBufferWithInsufficientContinuousSpace() {
        int continuous = 256;
        int messageLength = continuous;
        buffer.headSequence.set(2 * CAPACITY - continuous);
        buffer.tailSequence.set(CAPACITY + continuous);

        MutableBuffer message = UnsafeBuffer.allocateHeap(messageLength);
        assertFalse(buffer.write(MSG_TYPE, message, 0, messageLength));
    }

    @Test
    public void shouldWriteAndReadMessages() {
        int maxMessageLength = CAPACITY / 8;
        for (int messageLength = 0; messageLength <= maxMessageLength; messageLength++) {
            int recordLength = align(messageLength + HEADER_LENGTH, ALIGNMENT);
            int messageCount = CAPACITY / recordLength - 1;

            MutableBuffer message = UnsafeBuffer.allocateHeap(messageLength);
            for (int i = 0; i < messageLength; i++)
                message.putByte(i, (byte) i);

            for (int i = 0; i < messageCount; i++)
                assertTrue(buffer.write(MSG_TYPE, message, 0, messageLength));

            assertEquals(messageCount, buffer.read(verifier(message)));
            assertTrue(buffer.isEmpty());
            assertEquals(0, buffer.size());
        }
    }

    protected static MessageHandler verifier(Buffer message) {
        return (messageType, buf, offset, length) -> {
            assertEquals(MSG_TYPE, messageType);
            assertEquals(message.capacity(), length);

            for (int i = 0; i < length; i++)
                assertEquals(message.getByte(i), buf.getByte(offset + i));
        };
    }

    @Parameters(name = "{0}")
    public static Collection<RingBuffer> buffers() {
        return Arrays.asList(new SPSCRingBuffer(CAPACITY), new MPSCRingBuffer(CAPACITY));
    }

}
