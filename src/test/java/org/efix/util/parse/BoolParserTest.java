package org.efix.util.parse;

import org.junit.Test;

public class BoolParserTest extends AbstractParserTest {

    protected static final Verifier<Boolean> VERIFIER = string -> {
        if (string.length() == 1) {
            char c = string.charAt(0);
            if (c == 'Y')
                return true;
            else if (c == 'N')
                return false;
        }

        throw new IllegalArgumentException(string);
    };
    protected static final Parser<Boolean> PARSER = BoolParser::parseBool;

    @Test
    public void shouldParseBooleans() {
        shouldParse("Y");
        shouldParse("N");
    }

    @Test
    public void shouldFailParseBytes() {
        shouldFailParse("11");
        shouldFailParse("1");
        shouldFailParse("");
        shouldFailParse("=");
        shouldFailParse("1=");
        shouldFailParse("11=");
    }

    protected static void shouldParse(String string) {
        shouldParse(string, VERIFIER, PARSER);
    }

    protected static void shouldFailParse(String string) {
        shouldFailParse(string, PARSER);
    }

}
