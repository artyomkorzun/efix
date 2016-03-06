package org.efix;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;

public enum FixVersion {

    FIX42("FIX.4.2"),
    FIX43("FIX.4.3"),
    FIX44("FIX.4.4"),
    FIXT11("FIXT.1.1");

    private final ByteSequence beginString;

    FixVersion(String beginString) {
        this.beginString = ByteSequenceWrapper.of(beginString);
    }

    public ByteSequence beginString() {
        return beginString;
    }

}