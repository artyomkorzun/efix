package org.f1x.log;

import org.f1x.SessionComponent;
import org.f1x.util.buffer.Buffer;

public interface MessageLog extends SessionComponent {

    void log(boolean inbound, Buffer buffer, int offset, int length);

   // void log(boolean inbound, Buffer[] buffers); // TODO: experimental

}
