package org.f1x.message.field;


public class QuoteResponseLevel {

    public static final int NO_ACKNOWLEDGEMENT = 0;
    public static final int ACKNOWLEDGE_ONLY_NEGATIVE_OR_ERRONEOUS_QUOTES = 1;
    public static final int ACKNOWLEDGE_EACH_QUOTE_MESSAGES = 2;

}