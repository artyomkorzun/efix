package org.efix;

import org.efix.engine.Session;
import org.efix.engine.SessionContext;
import org.efix.message.FieldUtil;
import org.efix.message.Header;
import org.efix.message.field.MsgType;
import org.efix.message.parser.MessageParser;
import org.efix.state.SessionStatus;
import org.efix.util.ByteSequence;
import org.efix.util.concurrent.WorkerRunner;
import org.efix.util.concurrent.strategy.NoOpIdleStrategy;


public class Pong {

    public static void main(String[] args) {
        SessionId sessionId = new SessionId("Pong", "Ping");
        SessionContext context = new SessionContext(SampleConfiguration.HOST, SampleConfiguration.PORT, SessionType.ACCEPTOR, FixVersion.FIX44, sessionId);
        PongSession session = new PongSession(context);
        WorkerRunner runner = new WorkerRunner(session, new NoOpIdleStrategy());
        new Thread(runner, "Pong").start();
    }

    private static class PongSession extends Session {

        public PongSession(SessionContext context) {
            super(context);
        }

        @Override
        protected void onStatusUpdate(SessionStatus previous, SessionStatus current) {
            if (current == SessionStatus.APPLICATION_CONNECTED) {
                System.out.printf("Session %s -> %s connected%n", sessionId.senderCompId(), sessionId.targetCompId());
            } else if (current == SessionStatus.DISCONNECTED) {
                System.out.printf("Session %s -> %s disconnected%n", sessionId.senderCompId(), sessionId.targetCompId());
            }
        }

        @Override
        protected int doSendOutboundMessages() {
            return 0;
        }

        @Override
        protected void onAdminMessage(Header header, MessageParser parser) {
        }

        @Override
        protected void onAppMessage(Header header, MessageParser parser) {
            ByteSequence msgType = header.msgType();
            if (MsgType.ORDER_SINGLE.equals(msgType)) {
                int bodyStart = findBodyStart(parser);
                int bodyEnd = parser.end() - FieldUtil.CHECK_SUM_FIELD_LENGTH;
                int bodyLength = bodyEnd - bodyStart;

                sendAppMessage(MsgType.EXECUTION_REPORT, parser.buffer(), bodyStart, bodyLength);
            }
        }

        private int findBodyStart(MessageParser parser) {
            int bodyStart = 0;

            while (parser.hasRemaining()) {
                bodyStart = parser.offset();

                int tag = parser.parseTag();
                parser.parseValue();

                if (!FieldUtil.isHeader(tag)) {
                    break;
                }
            }

            return bodyStart;
        }

        @Override
        protected void onError(Exception e) {
            System.err.printf("Error occurred %s%n", e);
        }

    }

}
