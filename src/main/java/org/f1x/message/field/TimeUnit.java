package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class TimeUnit {

    public static final ByteSequence DAY = of("D");
    public static final ByteSequence HOUR = of("H");
    public static final ByteSequence SECOND = of("S");
    public static final ByteSequence MONTH = of("Mo");
    public static final ByteSequence WEEK = of("Wk");
    public static final ByteSequence YEAR = of("Yr");
    public static final ByteSequence MINUTE = of("Min");

}