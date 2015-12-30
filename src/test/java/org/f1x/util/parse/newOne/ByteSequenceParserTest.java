package org.f1x.util.parse.newOne;

import org.f1x.util.ByteSequence;
import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;
import org.junit.Test;

import static org.f1x.util.TestUtil.makeMessage;
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
        Buffer buffer = makeMessage(string + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        ByteSequence sequence = new ByteSequence();
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
            ByteSequenceParser.parseByteSequence(separator, buffer, offset, end, new ByteSequence());
            return null;
        };

        Parser<Object> emptyParser = (separator, buffer, offset, end) -> {
            ByteSequenceParser.parseByteSequence(separator, buffer, offset, end);
            return null;
        };

        shouldFailParse(string, sequenceParser, emptyParser);
    }

}
