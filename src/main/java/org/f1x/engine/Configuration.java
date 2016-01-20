package org.f1x.engine;

import static java.lang.Integer.getInteger;


public class Configuration {

    public static final String MESSAGE_QUEUE_SIZE_PROP_NAME = "f1x.message.queue.size";
    public static final String RECONNECT_INTERVAL_PROP_NAME = "f1x.reconnect.interval";
    public static final String MESSAGE_BUFFER_SIZE_PROP_NAME = "f1x.message.buffer.size";

    public static final String RECEIVE_BUFFER_SIZE_PROP_NAME = "f1x.receive.buffer.size";
    public static final String SEND_BUFFER_SIZE_PROP_NAME = "f1x.send.buffer.size";

    public static final String SOCKET_RECEIVE_BUFFER_SIZE_PROP_NAME = "f1x.socket.receive.buffer.size";
    public static final String SOCKET_SEND_BUFFER_SIZE_PROP_NAME = "f1x.socket.send.buffer.size";
    public static final String SOCKET_TCP_NO_DELAY_PROP_NAME = "f1x.socket.tcp.no.delay";

    public static final String HEARTBEAT_INTERVAL_PROP_NAME = "f1x.heartbeat.interval";
    public static final String HEARTBEAT_TIMEOUT_PROP_NAME = "f1x.heartbeat.timeout";

    public static final String LOGON_TIMEOUT_PROP_NAME = "f1x.logon.timeout";
    public static final String LOGOUT_TIMEOUT_PROP_NAME = "f1x.logout.timeout";

    protected static final int MESSAGE_QUEUE_SIZE = getInteger(MESSAGE_QUEUE_SIZE_PROP_NAME, 1 << 20);
    protected static final int RECONNECT_INTERVAL = getInteger(RECONNECT_INTERVAL_PROP_NAME, 15000);
    protected static final int MESSAGE_BUFFER_SIZE = getInteger(MESSAGE_BUFFER_SIZE_PROP_NAME, 1 << 10);

    protected static final int RECEIVE_BUFFER_SIZE = getInteger(RECEIVE_BUFFER_SIZE_PROP_NAME, 1 << 16);
    protected static final int SEND_BUFFER_SIZE = getInteger(SEND_BUFFER_SIZE_PROP_NAME, 1 << 16);

    protected static final int SOCKET_RECEIVE_BUFFER_SIZE = getInteger(SOCKET_RECEIVE_BUFFER_SIZE_PROP_NAME, 1 << 16);
    protected static final int SOCKET_SEND_BUFFER_SIZE = getInteger(SOCKET_SEND_BUFFER_SIZE_PROP_NAME, 1 << 16);
    protected static final boolean SOCKET_TCP_NO_DELAY = getBoolean(SOCKET_TCP_NO_DELAY_PROP_NAME, true);

    protected static final int HEARTBEAT_INTERVAL = getInteger(HEARTBEAT_INTERVAL_PROP_NAME, 30);
    protected static final int HEARTBEAT_TIMEOUT = getInteger(HEARTBEAT_TIMEOUT_PROP_NAME, 1000 * (HEARTBEAT_INTERVAL + 1));

    protected static final int LOGON_TIMEOUT = getInteger(LOGON_TIMEOUT_PROP_NAME, 2000);
    protected static final int LOGOUT_TIMEOUT = getInteger(LOGOUT_TIMEOUT_PROP_NAME, 2000);


    public static boolean getBoolean(String propName, boolean defaultValue) {
        boolean value = defaultValue;
        String propValue = System.getProperties().getProperty(propName);
        if (propValue != null)
            value = Boolean.parseBoolean(propValue);

        return value;
    }

}
