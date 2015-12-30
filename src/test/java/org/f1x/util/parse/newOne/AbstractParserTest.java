package org.f1x.util.parse.newOne;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class AbstractParserTest {

    protected static final byte SEPARATOR = '=';

    protected static void failIfParsed(String string, Action action) {
        try {
            action.execute();
            fail(String.format("should fail to parse \"%s\"", string));
        } catch (ParserException e) {
            assertTrue(true);
        }
    }

    protected interface Action {

        void execute();

    }

}
