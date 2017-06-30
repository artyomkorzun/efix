package org.efix.session;

import org.efix.util.buffer.Buffer;


public interface MessageHandler {

    void onMessage(Buffer buffer, int offset, int length);

}
