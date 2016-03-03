package org.efix.util.concurrent.buffer;

import org.efix.util.buffer.Buffer;

public interface MessageHandler {

    void onMessage(int messageType, Buffer buffer, int offset, int length);

}
