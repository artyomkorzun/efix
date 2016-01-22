package org.f1x;

public enum SessionType {

    INITIATOR, ACCEPTOR;

    public boolean initiator() {
        return this == INITIATOR;
    }

    public boolean acceptor() {
        return this == ACCEPTOR;
    }

}
