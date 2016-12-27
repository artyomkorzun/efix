package org.efix.store;

import org.efix.SessionComponent;
import org.efix.util.ByteSequence;
import org.efix.util.buffer.Buffer;

public interface MessageStore extends SessionComponent {

    void write(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length);

    /**
     * @return true if message is read otherwise false.
     */
    boolean read(int seqNum, Visitor visitor);

    /**
     * @param fromSeqNum sequence (inclusive) from which to read messages.
     * @param toSeqNum sequence (inclusive) to which to read messages.
     * @return read message count.
     */
    int read(int fromSeqNum, int toSeqNum, Visitor visitor);

    void clear();

    interface Visitor {

        void onMessage(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length);

    }

}
