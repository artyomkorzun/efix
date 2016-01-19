package org.f1x;

import org.f1x.util.ByteSequence;
import org.f1x.util.ByteSequenceWrapper;

public enum FIXVersion {

    FIX42("FIX.4.2"),
    FIX43("FIX.4.3"),
    FIX44("FIX.4.4"),
    FIXT11("FIXT.1.1");

    private final ByteSequence beginString;

    FIXVersion(String beginString) {
        this.beginString = ByteSequenceWrapper.of(beginString);
    }

    public ByteSequence beginString() {
        return beginString;
    }

}