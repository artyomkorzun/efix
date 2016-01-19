package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class DeskType {

    public static final ByteSequence AGENCY = of("A");
    public static final ByteSequence DERIVATIVES = of("D");
    public static final ByteSequence OTHER = of("O");
    public static final ByteSequence SALES = of("S");
    public static final ByteSequence TRADING = of("T");
    public static final ByteSequence ARBITRAGE = of("AR");
    public static final ByteSequence INTERNATIONAL = of("IN");
    public static final ByteSequence INSTITUTIONAL = of("IS");
    public static final ByteSequence PREFERRED_TRADING = of("PF");
    public static final ByteSequence PROPRIETARY = of("PR");
    public static final ByteSequence PROGRAM_TRADING = of("PT");

}