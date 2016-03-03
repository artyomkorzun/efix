package org.efix.util.format;

import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.junit.Test;

import static org.efix.util.StringUtil.asciiBytes;
import static org.junit.Assert.assertEquals;


public class ByteFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Byte> VERIFIER = value -> Character.toString((char) value.byteValue());
    protected static final Formatter<Byte> FORMATTER = ByteFormatter::formatByte;

    @Test
    public void shouldFormatBytes() {
        for (byte value = 0; value >= 0; value++)
            shouldFormatByte(value);
    }

    @Test
    public void shouldFormatByteArrays() {
        shouldFormatByteArray("hello", 0, 5);
        shouldFormatByteArray("hello", 2, 2);
    }

    @Test
    public void shouldFormatBuffers() {
        shouldFormatBuffer("hello", 0, 5);
        shouldFormatBuffer("hello", 2, 2);
    }

    protected void shouldFormatByte(byte value) {
        shouldFormat(value, VERIFIER, FORMATTER);
    }

    protected void shouldFormatByteArray(String value, int offset, int length) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(length);
        ByteFormatter.formatBytes(asciiBytes(value), offset, length, buffer, 0);

        String expected = value.substring(offset, offset + length);
        String actual = BufferUtil.toString(buffer);

        assertEquals("Fail to format " + value, expected, actual);
    }

    protected void shouldFormatBuffer(String value, int offset, int length) {
        MutableBuffer buffer = UnsafeBuffer.allocateHeap(length);
        ByteFormatter.formatBytes(BufferUtil.fromString(value), offset, length, buffer, 0);

        String expected = value.substring(offset, offset + length);
        String actual = BufferUtil.toString(buffer);

        assertEquals("Fail to format " + value, expected, actual);
    }

}
