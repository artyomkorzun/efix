package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class UnitOfMeasure {

    public static final ByteSequence METRIC_TONS = ByteSequenceWrapper.of("t");
    public static final ByteSequence BUSHELS = ByteSequenceWrapper.of("Bu");
    public static final ByteSequence TONS = ByteSequenceWrapper.of("tn");
    public static final ByteSequence BARRELS = ByteSequenceWrapper.of("Bbl");
    public static final ByteSequence BILLION_CUBIC_FEET = ByteSequenceWrapper.of("Bcf");
    public static final ByteSequence GALLONS = ByteSequenceWrapper.of("Gal");
    public static final ByteSequence MEGAWATT_HOURS = ByteSequenceWrapper.of("MWh");
    public static final ByteSequence US_DOLLARS = ByteSequenceWrapper.of("USD");
    public static final ByteSequence POUNDS = ByteSequenceWrapper.of("lbs");
    public static final ByteSequence ONE_MILLION_BTU = ByteSequenceWrapper.of("MMBtu");
    public static final ByteSequence MILLION_BARRELS = ByteSequenceWrapper.of("MMbbl");
    public static final ByteSequence TROY_OUNCES = ByteSequenceWrapper.of("oz_tr");

}