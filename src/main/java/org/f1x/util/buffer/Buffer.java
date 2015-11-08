package org.f1x.util.buffer;

import java.nio.ByteBuffer;

public interface Buffer {

    long addressOffset();

    int capacity();

    byte[] byteArray();

    ByteBuffer byteBuffer();

    long getLong(int offset);

    int getInt(int offset);

    double getDouble(int offset);

    float getFloat(int offset);

    short getShort(int offset);

    byte getByte(int offset);

    void getBytes(int offset, byte[] dst);

    void getBytes(int offset, byte[] dst, int dstOffset, int length);

    void getBytes(int offset, MutableBuffer dstBuffer, int dstIndex, int length);

    void getBytes(int offset, ByteBuffer dstBuffer, int length);

    void boundsCheck(int offset, int length);

}
