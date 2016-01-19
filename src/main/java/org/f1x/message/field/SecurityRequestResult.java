package org.f1x.message.field;


public class SecurityRequestResult {

    public static final int VALID_REQUEST = 0;
    public static final int INVALID_OR_UNSUPPORTED_REQUEST = 1;
    public static final int NO_INSTRUMENTS_FOUND_THAT_MATCH_SELECTION_CRITERIA = 2;
    public static final int NOT_AUTHORIZED_TO_RETRIEVE_INSTRUMENT_DATA = 3;
    public static final int INSTRUMENT_DATA_TEMPORARILY_UNAVAILABLE = 4;
    public static final int REQUEST_FOR_INSTRUMENT_DATA_NOT_SUPPORTED = 5;

}