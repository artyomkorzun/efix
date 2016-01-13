package org.f1x.util.format.newone;

import org.f1x.util.ByteSequence;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.MutableBuffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.junit.Test;

import static org.f1x.util.StringUtil.asciiBytes;
import static org.junit.Assert.assertEquals;

public class ByteFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Byte> VERIFIER = value -> Character.toString((char) value.byteValue());
    protected static final Formatter<Byte> FORMATTER = ByteFormatter::formatByte;

    @Test
    public void shouldFormatBytes() {
        for (byte value = 0; value != 127; value++)
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
        MutableBuffer buffer = new UnsafeBuffer(new byte[length]);
        ByteFormatter.formatBytes(asciiBytes(value), offset, length, buffer, new MutableInt(), length);

        String expected = value.substring(offset, offset + length);
        String actual = new ByteSequence(buffer).toString();

        assertEquals("Fail to format " + value, expected, actual);
    }

    protected void shouldFormatBuffer(String value, int offset, int length) {
        MutableBuffer buffer = new UnsafeBuffer(new byte[length]);
        ByteFormatter.formatBytes(new UnsafeBuffer(asciiBytes(value)), offset, length, buffer, new MutableInt(), length);

        String expected = value.substring(offset, offset + length);
        String actual = new ByteSequence(buffer).toString();

        assertEquals("Fail to format " + value, expected, actual);
    }

}
