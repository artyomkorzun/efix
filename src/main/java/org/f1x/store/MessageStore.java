package org.f1x.store;

import org.f1x.SessionComponent;
import org.f1x.util.buffer.Buffer;

public interface MessageStore extends SessionComponent {

    void write(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length);

    int read(int seqNum, Visitor visitor);

    void read(int fromSeqNum, int toSeqNum, Visitor visitor);

    void clear();

    interface Visitor {

        void onMessage(int seqNum, long sendingTime, CharSequence msgType, Buffer body, int offset, int length);

    }

}
