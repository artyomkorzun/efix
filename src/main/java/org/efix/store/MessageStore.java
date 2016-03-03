package org.efix.store;

import org.efix.SessionComponent;
import org.efix.util.ByteSequence;
import org.efix.util.buffer.Buffer;

public interface MessageStore extends SessionComponent {

    void write(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length);

    void read(int seqNum, Visitor visitor);

    void read(int fromSeqNum, int toSeqNum, Visitor visitor);

    void clear();

    interface Visitor {

        void onMessage(int seqNum, long time, ByteSequence msgType, Buffer body, int offset, int length);

    }

}
