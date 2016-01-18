package org.f1x.util.parse;

import org.f1x.util.BufferUtil;
import org.f1x.util.ByteSequenceWrapper;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteSequenceParserTest extends AbstractParserTest {

    @Test
    public void shouldParseSequences() {
        shouldParse("1");
        shouldParse("12");
        shouldParse("So I'm sorry \n");
    }

    @Test
    public void shouldFailParseSequences() {
        shouldFailParse("work");
        shouldFailParse("=");
        shouldFailParse("");
    }

    protected static void shouldParse(String string) {
        Buffer buffer = BufferUtil.fromString(string + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        ByteSequenceWrapper sequence = new ByteSequenceWrapper();
        ByteSequenceParser.parseByteSequence(SEPARATOR, buffer, offset, end, sequence);
        String actual = sequence.toString();

        assertEquals(string, actual);
        assertEquals(offset.value(), end);

        offset.value(0);
        ByteSequenceParser.parseByteSequence(SEPARATOR, buffer, offset, end);

        assertEquals(offset.value(), end);
    }

    protected static void shouldFailParse(String string) {
        Parser<Object> sequenceParser = (separator, buffer, offset, end) -> {
            ByteSequenceParser.parseByteSequence(separator, buffer, offset, end, new ByteSequenceWrapper());
            return null;
        };

        Parser<Object> emptyParser = (separator, buffer, offset, end) -> {
            ByteSequenceParser.parseByteSequence(separator, buffer, offset, end);
            return null;
        };

        shouldFailParse(string, sequenceParser, emptyParser);
    }

}
