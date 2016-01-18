package org.f1x.util;

import org.f1x.util.buffer.Buffer;

public interface ByteSequence extends CharSequence {

    byte byteAt(int index);

    Buffer buffer();

    default char charAt(int index) {
        return (char) byteAt(index);
    }

}
