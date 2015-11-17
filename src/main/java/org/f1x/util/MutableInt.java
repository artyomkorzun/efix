package org.f1x.util;

public final class MutableInt {

    private int value;

    public MutableInt() {
    }

    public MutableInt(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public void value(int newValue) {
        value = newValue;
    }

}
