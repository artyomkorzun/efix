package org.efix.message.field;


public class PosReqResult {

    public static final int VALID_REQUEST = 0;
    public static final int INVALID_OR_UNSUPPORTED_REQUEST = 1;
    public static final int NO_POSITIONS_FOUND_THAT_MATCH_CRITERIA = 2;
    public static final int NOT_AUTHORIZED_TO_REQUEST_POSITIONS = 3;
    public static final int REQUEST_FOR_POSITION_NOT_SUPPORTED = 4;
    public static final int OTHER = 99;

}