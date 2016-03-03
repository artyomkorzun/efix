package org.efix.connector.channel;

import org.efix.connector.ConnectionException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;

public interface Channel {

    int read(MutableBuffer buffer, int offset, int length) throws ConnectionException;

    int write(Buffer buffer, int offset, int length) throws ConnectionException;

}
