package org.efix;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class SessionId {

    protected final ByteSequence senderCompId;
    protected final ByteSequence senderSubId;
    protected final ByteSequence targetCompId;
    protected final ByteSequence targetSubId;

    public SessionId(String senderCompId, String targetCompId) {
        this(byteSequence(senderCompId), byteSequence(targetCompId));
    }

    public SessionId(String senderCompId, String senderSubId, String targetCompId, String targetSubId) {
        this(byteSequence(senderCompId), byteSequence(senderSubId), byteSequence(targetCompId), byteSequence(targetSubId));
    }

    public SessionId(ByteSequence senderCompId, ByteSequence senderSubId, ByteSequence targetCompId, ByteSequence targetSubId) {
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
    }

    public SessionId(ByteSequence senderCompId, ByteSequence targetCompId) {
        this(senderCompId, null, targetCompId, null);
    }

    public ByteSequence senderCompId() {
        return senderCompId;
    }

    public ByteSequence senderSubId() {
        return senderSubId;
    }

    public ByteSequence targetCompId() {
        return targetCompId;
    }

    public ByteSequence targetSubId() {
        return targetSubId;
    }

    protected static ByteSequenceWrapper byteSequence(String string) {
        return string == null ? null : ByteSequenceWrapper.of(string);
    }

}
