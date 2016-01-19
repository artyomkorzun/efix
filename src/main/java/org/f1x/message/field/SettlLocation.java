package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class SettlLocation {

    public static final ByteSequence CEDEL = of("CED");
    public static final ByteSequence DEPOSITORY_TRUST_COMPANY = of("DTC");
    public static final ByteSequence EUROCLEAR = of("EUR");
    public static final ByteSequence FEDERAL_BOOK_ENTRY = of("FED");
    public static final ByteSequence LOCAL_MARKET_SETTLE_LOCATION = of("ISO");
    public static final ByteSequence PHYSICAL = of("PNY");
    public static final ByteSequence PARTICIPANT_TRUST_COMPANY = of("PTC");

}