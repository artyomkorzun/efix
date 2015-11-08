package org.f1x.util.buffer;

import java.nio.ByteBuffer;

public interface MutableBuffer extends Buffer {

    void wrap(byte[] buffer);

    void wrap(byte[] buffer, int offset, int length);

    void wrap(ByteBuffer buffer);

    void wrap(ByteBuffer buffer, int offset, int length);

    void wrap(Buffer buffer);

    void wrap(Buffer buffer, int offset, int length);

    void wrap(long address, int length);


    void setMemory(int offset, int length, byte value);


    void putLong(int offset, long value);

    void putInt(int offset, int value);

    void putDouble(int offset, double value);

    void putFloat(int offset, float value);

    void putShort(int offset, short value);

    void putByte(int offset, byte value);

    void putBytes(int offset, byte[] src);

    void putBytes(int offset, byte[] src, int srcOffset, int length);

    void putBytes(int offset, ByteBuffer srcBuffer, int length);

    void putBytes(int offset, ByteBuffer srcBuffer, int srcOffset, int length);

    void putBytes(int offset, Buffer buffer);

    void putBytes(int offset, Buffer srcBuffer, int srcOffset, int length);

}
