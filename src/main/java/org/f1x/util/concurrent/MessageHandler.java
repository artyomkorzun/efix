package org.f1x.util.concurrent;

import org.f1x.util.buffer.Buffer;

public interface MessageHandler {

    void onMessage(int messageType, Buffer buffer, int offset, int length);

}
