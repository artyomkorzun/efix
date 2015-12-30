package org.f1x.util.parse.newOne;

import org.f1x.util.MutableInt;
import org.f1x.util.buffer.Buffer;

import static org.f1x.util.TestUtil.makeMessage;
import static org.junit.Assert.*;

public abstract class AbstractParserTest {

    protected static final byte SEPARATOR = '=';

    @SafeVarargs
    protected static <T> void shouldParse(String string, ParserVerifier<T> verifier, Parser<T>... parsers) {
        Buffer buffer = makeMessage(string + (char) SEPARATOR);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        T expected = verifier.parse(string);
        for (Parser<T> parser : parsers) {
            offset.value(0);
            T actual = parser.parse(SEPARATOR, buffer, offset, end);

            assertEquals(end, offset.value());
            assertEquals(string, expected, actual);
        }
    }

    protected static void shouldFailParse(String string, Parser<?>... parsers) {
        Buffer buffer = makeMessage(string);
        MutableInt offset = new MutableInt();
        int end = buffer.capacity();

        for (Parser<?> parser : parsers) {
            offset.value(0);
            try {
                parser.parse(SEPARATOR, buffer, offset, end);
                fail(String.format("should fail to parse \"%s\"", string));
            } catch (ParserException e) {
                assertTrue(true);
            }
        }
    }

    protected interface Parser<T> {

        T parse(byte separator, Buffer buffer, MutableInt offset, int end);

    }

    protected interface ParserVerifier<T> {

        T parse(String string);

    }

}
