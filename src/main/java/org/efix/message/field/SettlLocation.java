package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class SettlLocation {

    public static final ByteSequence CEDEL = ByteSequenceWrapper.of("CED");
    public static final ByteSequence DEPOSITORY_TRUST_COMPANY = ByteSequenceWrapper.of("DTC");
    public static final ByteSequence EUROCLEAR = ByteSequenceWrapper.of("EUR");
    public static final ByteSequence FEDERAL_BOOK_ENTRY = ByteSequenceWrapper.of("FED");
    public static final ByteSequence LOCAL_MARKET_SETTLE_LOCATION = ByteSequenceWrapper.of("ISO");
    public static final ByteSequence PHYSICAL = ByteSequenceWrapper.of("PNY");
    public static final ByteSequence PARTICIPANT_TRUST_COMPANY = ByteSequenceWrapper.of("PTC");

}