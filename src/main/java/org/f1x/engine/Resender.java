package org.f1x.engine;

import org.f1x.store.MessageStore;
import org.f1x.util.buffer.Buffer;

public class Resender implements MessageStore.Visitor {

    private SessionProcessor processor;
    protected int lastSeqNum;

    public Resender(SessionProcessor processor) {
        this.processor = processor;
    }

    public void resendMessages(int fromSeqNum, int toSeqNum, MessageStore store) {
        lastSeqNum = fromSeqNum - 1;
        store.read(fromSeqNum, toSeqNum, this);
        if (toSeqNum > lastSeqNum)
            processor.sendSequenceReset(true, lastSeqNum + 1, toSeqNum + 1);
    }

    @Override
    public void onMessage(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length) {
        if (processor.onResendMessage(seqNum, sendingTime, msgType, body, offset, length)) {
            if (seqNum - lastSeqNum > 1)
                processor.sendSequenceReset(true, lastSeqNum + 1, seqNum);

            processor.resendMessage(seqNum, sendingTime, msgType, body, offset, length);
            lastSeqNum = seqNum;
        }

    }

}
