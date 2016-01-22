package org.f1x.util.format;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;

import static org.junit.Assert.*;

public abstract class AbstractFormatterTest {

    @SafeVarargs
    protected static <T> void shouldFormat(T object, Verifier<T> verifier, Formatter<T>... formatters) {
        String expected = verifier.format(object);
        MutableBuffer buffer = new UnsafeBuffer(new byte[expected.length()]);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        for (Formatter<T> formatter : formatters) {
            offset.value(0);
            formatter.format(object, buffer, offset, end);
            String actual = BufferUtil.toString(buffer, 0, offset.value());
            assertEquals("Fail to parse " + object, expected, actual);
        }
    }

    @SafeVarargs
    protected static <T> void shouldFailFormat(T object, int offset, int capacity, Formatter<T>... formatters) {
        MutableBuffer buffer = new UnsafeBuffer(new byte[capacity]);

        for (Formatter<T> formatter : formatters) {
            try {
                formatter.format(object, buffer, new MutableInt(offset), capacity);
                fail("should fail to parse " + object);
            } catch (FormatterException e) {
                assertTrue(true);
            }
        }
    }

    protected interface Formatter<T> {

        void format(T object, MutableBuffer buffer, MutableInt offset, int end);

    }

    protected interface Verifier<T> {

        String format(T object);

    }

}
