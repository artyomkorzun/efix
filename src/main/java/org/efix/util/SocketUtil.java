package org.efix.util;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;


public class SocketUtil {

    public static SocketAddress getLocalAddress(SocketChannel channel) {
        try {
            return channel.getLocalAddress();
        } catch (IOException e) {
            throw LangUtil.rethrow(e);
        }
    }

    public static SocketAddress getRemoteAddress(SocketChannel channel) {
        try {
            return channel.getRemoteAddress();
        } catch (IOException e) {
            throw LangUtil.rethrow(e);
        }
    }

}
