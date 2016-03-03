package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class DeskType {

    public static final ByteSequence AGENCY = ByteSequenceWrapper.of("A");
    public static final ByteSequence DERIVATIVES = ByteSequenceWrapper.of("D");
    public static final ByteSequence OTHER = ByteSequenceWrapper.of("O");
    public static final ByteSequence SALES = ByteSequenceWrapper.of("S");
    public static final ByteSequence TRADING = ByteSequenceWrapper.of("T");
    public static final ByteSequence ARBITRAGE = ByteSequenceWrapper.of("AR");
    public static final ByteSequence INTERNATIONAL = ByteSequenceWrapper.of("IN");
    public static final ByteSequence INSTITUTIONAL = ByteSequenceWrapper.of("IS");
    public static final ByteSequence PREFERRED_TRADING = ByteSequenceWrapper.of("PF");
    public static final ByteSequence PROPRIETARY = ByteSequenceWrapper.of("PR");
    public static final ByteSequence PROGRAM_TRADING = ByteSequenceWrapper.of("PT");

}