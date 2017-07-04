package org.efix.util.parse.exp;

import org.efix.util.parse.ByteParser;
import org.junit.Test;


public class ByteParserTest extends AbstractParserTest {

    protected static final Verifier<Byte> VERIFIER = string -> {
        if (string.length() != 1) {
            throw new IllegalArgumentException(string);
        }

        return (byte) string.charAt(0);
    };
    protected static final Parser<Byte> PARSER = ByteParser::parseByte;

    @Test
    public void shouldParseBytes() {
        shouldParse("1");
        shouldParse("e");
        shouldParse("\n");
    }

    @Test
    public void shouldFailParseBytes() {
        shouldFailParse("11");
        shouldFailParse("ez");
        shouldFailParse("1=");
        shouldFailParse("");
        shouldFailParse("11=");
    }

    protected static void shouldParse(String string) {
        shouldParse(string, VERIFIER, PARSER);
    }

    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

}
