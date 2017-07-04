package org.efix.util.parse.exp;

import org.efix.message.FieldException;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.BufferUtil;

import static org.junit.Assert.*;


public abstract class AbstractParserTest {

    protected static final int TAG = 1;
    protected static final byte SEPARATOR = '=';

    @SafeVarargs
    protected static <T> void shouldParse(String string, Verifier<T> verifier, Parser<T>... parsers) {
        Buffer buffer = BufferUtil.fromString(string);
        int end = buffer.capacity();

        T expected = verifier.parse(string);
        for (Parser<T> parser : parsers) {
            T actual = parser.parse(TAG, buffer, 0, end);
            assertEquals(string, expected, actual);
        }
    }

    protected static void shouldFailParse(String string, Parser<?>... parsers) {
        Buffer buffer = BufferUtil.fromString(string);
        int end = buffer.capacity();

        for (Parser<?> parser : parsers) {
            try {
                parser.parse(TAG, buffer, 0, end);
                fail(String.format("should fail to parse \"%s\"", string));
            } catch (FieldException e) {
                assertTrue(true);
            }
        }
    }

    @FunctionalInterface
    protected interface Parser<T> {

        T parse(int tag, Buffer buffer, int offset, int end);

    }

    @FunctionalInterface
    protected interface Verifier<T> {

        T parse(String string);

    }

}
