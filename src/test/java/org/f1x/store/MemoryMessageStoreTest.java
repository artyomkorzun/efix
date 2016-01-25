package org.f1x.store;

import org.f1x.message.field.MsgType;
import org.f1x.util.ByteSequence;
import org.f1x.util.InsufficientSpaceException;
import org.f1x.util.TestUtil;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Before;
import org.junit.Test;

import static org.f1x.store.MemoryMessageStore.ALIGNMENT;
import static org.f1x.store.MemoryMessageStore.EXTRA_SIZE;
import static org.f1x.util.BitUtil.align;
import static org.junit.Assert.assertEquals;

public class MemoryMessageStoreTest {

    protected static final int CAPACITY = 1 << 12;

    protected static final ByteSequence MSG_TYPE = MsgType.ORDER_SINGLE;
    protected static final int MAX_BODY_LENGTH = CAPACITY / 8 - EXTRA_SIZE - MSG_TYPE.length();

    protected static final long TIME = TestUtil.parseTimestamp("20160125-00:00:00");

    protected static final int SEQ_NUM_INCREMENT = 2;

    protected final MessageStore store = new MemoryMessageStore(CAPACITY);

    @Before
    public void setUp() {
        store.clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionSeqNumNotMoreLast() {
        int msgSeqNum = 1;
        int bodyLength = 128;
        Buffer body = UnsafeBuffer.allocateHeap(bodyLength);

        store.write(msgSeqNum, TIME, MSG_TYPE, body, 0, bodyLength);
        store.write(msgSeqNum, TIME, MSG_TYPE, body, 0, bodyLength);
    }

    @Test(expected = InsufficientSpaceException.class)
    public void shouldThrowExceptionEntrySizeExceedsMax() {
        int msgSeqNum = 1;
        int bodyLength = CAPACITY / 8;
        Buffer body = UnsafeBuffer.allocateHeap(bodyLength);

        store.write(msgSeqNum, TIME, MSG_TYPE, body, 0, bodyLength);
    }

    @Test
    public void shouldWriteAndReadMessages() {
        Verifier verifier = new Verifier();

        for (int bodyLength = 0, seqNum = 1; bodyLength < MAX_BODY_LENGTH; bodyLength++) {
            int entrySize = entrySize(bodyLength);
            int messageCount = CAPACITY / entrySize - 1;

            Buffer body = makeBody(bodyLength);

            int startSeqNum = seqNum;
            for (int i = 0; i < messageCount; i++, seqNum += SEQ_NUM_INCREMENT)
                store.write(seqNum, TIME, MSG_TYPE, body, 0, bodyLength);

            int endSeqNum = seqNum - SEQ_NUM_INCREMENT;

            verifier.verify(messageCount, startSeqNum, body, store, startSeqNum, endSeqNum);
            verifier.verify(messageCount, startSeqNum, body, store, startSeqNum - 1, endSeqNum + 1);
        }
    }

    @Test
    public void shouldNotReadTruncatedMessage() {
        Verifier verifier = new Verifier();

        Buffer bigBody = makeBody(MAX_BODY_LENGTH);
        Buffer smallBody = makeBody(0);

        int messageCount = CAPACITY / entrySize(MAX_BODY_LENGTH);
        int seqNum = 1;

        // 1 3 5 7 9 11 13 15
        for (int i = 0; i < messageCount; i++, seqNum += SEQ_NUM_INCREMENT)
            store.write(seqNum, TIME, MSG_TYPE, bigBody, 0, bigBody.capacity());

        // 17
        store.write(seqNum, TIME, MSG_TYPE, smallBody, 0, smallBody.capacity());

        verifier.verify(messageCount - 1, 3, bigBody, store, 1, seqNum - 1);
        verifier.verify(1, seqNum, smallBody, store, seqNum - 1, seqNum + 99);
    }

    protected static Buffer makeBody(int length) {
        MutableBuffer body = UnsafeBuffer.allocateHeap(length);
        for (int i = 0; i < length; i++)
            body.putByte(i, (byte) i);

        return body;
    }

    protected static int entrySize(int bodyLength) {
        return align(bodyLength + EXTRA_SIZE + MSG_TYPE.length(), ALIGNMENT);
    }

    protected static class Verifier implements MessageStore.Visitor {

        protected Buffer body;

        protected int seqNum;
        protected int messageCount;

        @Override
        public void onMessage(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length) {
            assertEquals(this.seqNum, seqNum);
            assertEquals(TIME, time);
            assertMessageType(msgType);
            assertBody(body, offset, length);

            this.seqNum += SEQ_NUM_INCREMENT;
            this.messageCount++;
        }

        public void verify(int messageCount, int seqNum, Buffer body, MessageStore store, int fromSeqNum, int toSeqNum) {
            this.body = body;
            this.seqNum = seqNum;
            this.messageCount = 0;

            store.read(fromSeqNum, toSeqNum, this);

            assertEquals(messageCount, this.messageCount);
        }

        protected void assertMessageType(ByteSequence msgType) {
            assertEquals(MSG_TYPE.toString(), msgType.toString());
        }

        protected void assertBody(Buffer body, int offset, int length) {
            for (int i = 0; i < length; i++)
                assertEquals(this.body.getByte(i), body.getByte(offset + i));
        }

    }

}
