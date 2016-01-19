package org.f1x;

import org.f1x.util.ByteSequence;

public interface SessionID {

    ByteSequence senderCompId();

    ByteSequence senderSubId();

    ByteSequence targetCompId();

    ByteSequence targetSubId();

}
