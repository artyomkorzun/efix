package org.efix;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class SessionId {

    protected final ByteSequence senderCompId;
    protected final ByteSequence senderSubId;
    protected final ByteSequence targetCompId;
    protected final ByteSequence targetSubId;
    protected final ByteSequence senderLocationId;

    public SessionId(String senderCompId, String targetCompId) {
        this(byteSequence(senderCompId), byteSequence(targetCompId));
    }

    public SessionId(String senderCompId, String senderSubId, String targetCompId, String targetSubId) {
        this(byteSequence(senderCompId), byteSequence(senderSubId), byteSequence(targetCompId), byteSequence(targetSubId));
    }

    public SessionId(String senderCompId, String senderSubId, String targetCompId, String targetSubId, String senderLocationId) {
        this(
                byteSequence(senderCompId),
                byteSequence(senderSubId),
                byteSequence(targetCompId),
                byteSequence(targetSubId),
                byteSequence(senderLocationId)
        );
    }

    public SessionId(ByteSequence senderCompId, ByteSequence targetCompId) {
        this(senderCompId, null, targetCompId, null);
    }

    public SessionId(ByteSequence senderCompId, ByteSequence senderSubId, ByteSequence targetCompId, ByteSequence targetSubId) {
        this(senderCompId, senderSubId, targetCompId, targetSubId, null);
    }

    public SessionId(ByteSequence senderCompId,
                     ByteSequence senderSubId,
                     ByteSequence targetCompId,
                     ByteSequence targetSubId,
                     ByteSequence senderLocationId) {
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
        this.senderLocationId = senderLocationId;
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

    public ByteSequence senderLocationId() {
        return senderLocationId;
    }

    protected static ByteSequenceWrapper byteSequence(String string) {
        return string == null ? null : ByteSequenceWrapper.of(string);
    }

}
