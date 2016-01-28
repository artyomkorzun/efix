package org.f1x.util.format;

import org.f1x.util.buffer.BufferUtil;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CharFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Character> VERIFIER = Object::toString;
    protected static final Formatter<Character> FORMATTER = CharFormatter::formatChar;

    @Test
    public void shouldFormatChars() {
        for (char value = 0; value <= 127; value++)
            shouldFormatChar(value);
    }

    @Test
    public void shouldFormatCharSequences() {
        shouldFormatChars("hello", 0, 5);
        shouldFormatChars("hello", 2, 2);
    }

    protected void shouldFormatChar(char value) {
        shouldFormat(value, VERIFIER, FORMATTER);
    }

    protected void shouldFormatChars(String value, int offset, int length) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(length);
        CharFormatter.formatChars(value, offset, length, buffer, 0);

        String expected = value.substring(offset, offset + length);
        String actual = BufferUtil.toString(buffer);

        assertEquals("Fail to format " + value, expected, actual);
    }

}
