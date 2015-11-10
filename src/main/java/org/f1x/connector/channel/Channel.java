package org.f1x.connector.channel;

import org.f1x.connector.ConnectionException;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.MutableBuffer;

public interface Channel {

    int read(MutableBuffer buffer, int offset, int length) throws ConnectionException;

    int write(Buffer buffer, int offset, int length) throws ConnectionException;

}
