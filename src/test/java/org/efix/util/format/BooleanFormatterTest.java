package org.efix.util.format;

import org.junit.Test;


public class BooleanFormatterTest extends AbstractFormatterTest {

    protected static final Verifier<Boolean> VERIFIER = value -> value ? "Y" : "N";
    protected static final Formatter<Boolean> FORMATTER = BooleanFormatter::formatBoolean;

    @Test
    public void shouldFormatBooleans() {
        shouldFormat(true);
        shouldFormat(false);
    }

    protected static void shouldFormat(boolean value) {
        shouldFormat(value, VERIFIER, FORMATTER);
    }

}
