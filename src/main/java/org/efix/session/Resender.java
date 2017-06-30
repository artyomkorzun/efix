package org.efix.session;

import org.efix.store.MessageStore;
import org.efix.util.ByteSequence;
import org.efix.util.buffer.Buffer;


public class Resender implements MessageStore.Visitor {

    protected final Session session;

    protected int lastSeqNum;

    public Resender(Session session) {
        this.session = session;
    }

    public void resendMessages(int fromSeqNum, int toSeqNum, MessageStore store) {
        lastSeqNum = fromSeqNum - 1;
        store.read(fromSeqNum, toSeqNum, this);
        if (toSeqNum > lastSeqNum)
            session.sendSequenceReset(true, lastSeqNum + 1, toSeqNum + 1);
    }

    @Override
    public void onMessage(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length) {
        if (session.onResendMessage(seqNum, time, msgType, body, offset, length)) {
            if (seqNum - lastSeqNum > 1)
                session.sendSequenceReset(true, lastSeqNum + 1, seqNum);

            session.resendMessage(seqNum, time, msgType, body, offset, length);
            lastSeqNum = seqNum;
        }
    }

}
