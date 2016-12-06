package org.efix.log.filter;

import org.efix.engine.SessionUtil;
import org.efix.message.field.MsgType;
import org.efix.message.parser.FastMessageParser;
import org.efix.message.parser.MessageParser;
import org.efix.util.ByteSequenceWrapper;
import org.efix.util.buffer.Buffer;


public class HeartbeatFilter implements Filter {

    protected final MessageParser parser = new FastMessageParser();
    protected final ByteSequenceWrapper msgType = new ByteSequenceWrapper();

    @Override
    public boolean filter(boolean inbound, long time, Buffer message, int offset, int length) {
        MessageParser parser = this.parser;
        ByteSequenceWrapper msgType = this.msgType;

        parser.wrap(message, offset, length);
        SessionUtil.parseBeginString(parser);
        SessionUtil.parseBodyLength(parser);
        SessionUtil.parseMessageType(parser, msgType);

        return MsgType.HEARTBEAT.equals(msgType);
    }

}
