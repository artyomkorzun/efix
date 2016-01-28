package org.f1x.util.format;

import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

import static org.junit.Assert.*;

public abstract class AbstractFormatterTest {

    protected static <T> void shouldFormat(T object, Verifier<T> verifier, Formatter<T> formatter) {
        String expected = verifier.format(object);
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(expected.length());

        int length = formatter.format(object, buffer, 0);
        String actual = BufferUtil.toString(buffer, 0, length);
        assertEquals("Fail to format " + object, expected, actual);

    }

    protected static <T> void shouldFailFormat(T object, int length, Formatter<T> formatter) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(length);

        try {
            formatter.format(object, buffer, 0);
            fail("Should fail to format " + object);
        } catch (FormatterException e) {
            assertTrue(true);
        }
    }

    protected interface Formatter<T> {

        int format(T object, MutableBuffer buffer, int offset);

    }

    protected interface Verifier<T> {

        String format(T object);

    }

}
