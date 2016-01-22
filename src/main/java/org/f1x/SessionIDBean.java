package org.f1x;

import org.f1x.util.ByteSequence;
import org.f1x.util.ByteSequenceWrapper;

public class SessionIDBean implements SessionID {

    protected ByteSequence senderCompId;
    protected ByteSequence senderSubId;
    protected ByteSequence targetCompId;
    protected ByteSequence targetSubId;

    public SessionIDBean(ByteSequence senderCompId, ByteSequence targetCompId) {
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
    }

    public SessionIDBean(ByteSequence senderCompId, ByteSequence senderSubId, ByteSequence targetCompId, ByteSequence targetSubId) {
        this.senderCompId = senderCompId;
        this.senderSubId = senderSubId;
        this.targetCompId = targetCompId;
        this.targetSubId = targetSubId;
    }

    public SessionIDBean(String senderCompId, String targetCompId) {
        senderCompId(senderCompId);
        targetCompId(targetCompId);
    }

    public SessionIDBean(String senderCompId, String senderSubId, String targetCompId, String targetSubId) {
        senderCompId(senderCompId);
        senderSubId(senderSubId);
        targetCompId(targetCompId);
        targetSubId(targetSubId);
    }

    @Override
    public ByteSequence senderCompId() {
        return senderCompId;
    }

    @Override
    public ByteSequence senderSubId() {
        return senderSubId;
    }

    @Override
    public ByteSequence targetCompId() {
        return targetCompId;
    }

    @Override
    public ByteSequence targetSubId() {
        return targetSubId;
    }

    public SessionIDBean senderCompId(ByteSequence senderCompId) {
        this.senderCompId = senderCompId;
        return this;
    }

    public SessionIDBean senderSubId(ByteSequence senderSubId) {
        this.senderSubId = senderSubId;
        return this;
    }

    public SessionIDBean targetCompId(ByteSequence targetCompId) {
        this.targetCompId = targetCompId;
        return this;
    }

    public SessionIDBean targetSubId(ByteSequence targetSubId) {
        this.targetSubId = targetSubId;
        return this;
    }

    public SessionIDBean senderCompId(String senderCompId) {
        this.senderCompId = ByteSequenceWrapper.of(senderCompId);
        return this;
    }

    public SessionIDBean senderSubId(String senderSubId) {
        this.senderSubId = ByteSequenceWrapper.of(senderSubId);
        return this;
    }

    public SessionIDBean targetCompId(String targetCompId) {
        this.targetCompId = ByteSequenceWrapper.of(targetCompId);
        return this;
    }

    public SessionIDBean targetSubId(String targetSubId) {
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
