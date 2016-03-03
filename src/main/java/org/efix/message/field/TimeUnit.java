package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class TimeUnit {

    public static final ByteSequence DAY = ByteSequenceWrapper.of("D");
    public static final ByteSequence HOUR = ByteSequenceWrapper.of("H");
    public static final ByteSequence SECOND = ByteSequenceWrapper.of("S");
    public static final ByteSequence MONTH = ByteSequenceWrapper.of("Mo");
    public static final ByteSequence WEEK = ByteSequenceWrapper.of("Wk");
    public static final ByteSequence YEAR = ByteSequenceWrapper.of("Yr");
    public static final ByteSequence MINUTE = ByteSequenceWrapper.of("Min");

}