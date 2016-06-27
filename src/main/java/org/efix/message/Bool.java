package org.efix.message;

public enum Bool {

    YES(true), NO(false);

    private final boolean value;

    Bool(boolean value) {
        this.value = value;
    }

    public boolean yes(){
        return value;
    }

    public boolean no() {
        return !value;
    }

}
