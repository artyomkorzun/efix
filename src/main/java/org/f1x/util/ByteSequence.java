package org.f1x.util;

import org.f1x.util.buffer.Buffer;

public interface ByteSequence extends CharSequence {

    byte byteAt(int index);

    Buffer buffer();

    default char charAt(int index) {
        return (char) byteAt(index);
    }

    default boolean equals(CharSequence sequence) {
        if (sequence == null)
            return false;

        int length = length();
        if (sequence.length() != length)
            return false;

        for (int i = 0; i < length; i++)
            if (byteAt(i) != sequence.charAt(i))
                return false;

        return true;
    }

    default boolean equals(ByteSequence sequence) {
        if (sequence == null)
            return false;

        int length = length();
        if (sequence.length() != length)
            return false;

        for (int i = 0; i < length; i++)
            if (byteAt(i) != sequence.byteAt(i))
                return false;

        return true;
    }

}
