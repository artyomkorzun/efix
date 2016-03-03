package org.efix.log;

import org.efix.SessionComponent;
import org.efix.util.buffer.Buffer;


public interface MessageLog extends SessionComponent {

    void log(boolean inbound, long time, Buffer message, int offset, int length);

}
