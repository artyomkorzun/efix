package org.efix.message;

import org.efix.util.ByteSequenceWrapper;

public class TestRequest {

    protected final ByteSequenceWrapper testReqID = new ByteSequenceWrapper();

    public ByteSequenceWrapper testReqID() {
        return testReqID;
    }

}
