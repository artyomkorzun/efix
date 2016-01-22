package org.f1x;

import org.f1x.util.ByteSequence;
import org.f1x.util.ByteSequenceWrapper;


public class SessionID {

    protected ByteSequence senderCompId;
    protected ByteSequence senderSubId;
    protected ByteSequence targetCompId;
    protected ByteSequence targetSubId;

    public SessionID(ByteSequence senderCompId, ByteSequence targetCompId) {
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
    }

    public SessionID(ByteSequence senderCompId, ByteSequence senderSubId, ByteSequence targetCompId, ByteSequence targetSubId) {
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
    }

    public SessionID(String senderCompId, String targetCompId) {
        senderCompId(senderCompId);
        targetCompId(targetCompId);
    }

    public SessionID(String senderCompId, String senderSubId, String targetCompId, String targetSubId) {
        senderCompId(senderCompId);
        senderSubId(senderSubId);
        targetCompId(targetCompId);
        targetSubId(targetSubId);
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

    public SessionID senderCompId(ByteSequence senderCompId) {
        this.senderCompId = senderCompId;
        return this;
    }

    public SessionID senderSubId(ByteSequence senderSubId) {
        this.senderSubId = senderSubId;
        return this;
    }

    public SessionID targetCompId(ByteSequence targetCompId) {
        this.targetCompId = targetCompId;
        return this;
    }

    public SessionID targetSubId(ByteSequence targetSubId) {
        this.targetSubId = targetSubId;
        return this;
    }

    public SessionID senderCompId(String senderCompId) {
        this.senderCompId = ByteSequenceWrapper.of(senderCompId);
        return this;
    }

    public SessionID senderSubId(String senderSubId) {
        this.senderSubId = ByteSequenceWrapper.of(senderSubId);
        return this;
    }

    public SessionID targetCompId(String targetCompId) {
        this.targetCompId = ByteSequenceWrapper.of(targetCompId);
        return this;
    }

    public SessionID targetSubId(String targetSubId) {
        this.targetSubId = ByteSequenceWrapper.of(targetSubId);
        return this;
    }

    @Override
    public String toString() {
        return "senderCompId=" + senderCompId +
                ", senderSubId=" + senderSubId +
                ", targetCompId=" + targetCompId +
                ", targetSubId=" + targetSubId;
    }

}
