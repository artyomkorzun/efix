package org.efix.util.parse;

import org.junit.Test;

public class CharParserTest extends AbstractParserTest {

    protected static final Verifier<Character> VERIFIER = string -> {
        if (string.length() != 1)
            throw new IllegalArgumentException(string);

        return string.charAt(0);
    };
    protected static final Parser<Character> PARSER = CharParser::parseChar;

    @Test
    public void shouldParseChars() {
        shouldParse("1");
        shouldParse("e");
        shouldParse("\n");
    }

    @Test
    public void shouldFailParseChars() {
        shouldFailParse("11");
        shouldFailParse("ez");
        shouldFailParse("=");
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
