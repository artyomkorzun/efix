package org.f1x.util.buffer;


public interface AtomicBuffer extends MutableBuffer {

    void verifyAlignment();


    long getLongVolatile(int index);

    void putLongVolatile(int index, long value);

    void putLongOrdered(int index, long value);

    boolean compareAndSetLong(int index, long expectedValue, long updateValue);

    long getAndSetLong(int index, long value);

    long getAndAddLong(int index, long delta);


    int getIntVolatile(int index);

    void putIntVolatile(int index, int value);

    void putIntOrdered(int index, int value);

    boolean compareAndSetInt(int index, int expectedValue, int updateValue);

    int getAndSetInt(int index, int value);

    int getAndAddInt(int index, int delta);


    short getShortVolatile(int index);

    void putShortVolatile(int index, short value);


    byte getByteVolatile(int index);

    void putByteVolatile(int index, byte value);

}
