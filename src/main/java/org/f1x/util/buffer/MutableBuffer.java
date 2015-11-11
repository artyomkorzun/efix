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

    void putLong(int index, long value);

    void putInt(int index, int value);

    void putDouble(int index, double value);

    void putFloat(int index, float value);

    void putShort(int index, short value);

    void putChar(int index, char value);

    void putByte(int index, byte value);

    void putBytes(int index, byte[] src);

    void putBytes(int index, byte[] src, int srcOffset, int length);

    void putBytes(int index, ByteBuffer srcBuffer, int length);

    void putBytes(int index, ByteBuffer srcBuffer, int srcOffset, int length);

    void putBytes(int index, Buffer srcBuffer);

    void putBytes(int index, Buffer srcBuffer, int srcOffset, int length);

}
