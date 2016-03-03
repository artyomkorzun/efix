package org.efix.message.field;

import org.efix.util.ByteSequence;

import static org.efix.util.ByteSequenceWrapper.of;


public class DeskOrderHandlingInst {

    public static final ByteSequence IMBALANCE_ONLY = of("IO");
    public static final ByteSequence NOT_HELD = of("NH");
    public static final ByteSequence TRAILING_STOP = of("TS");
    public static final ByteSequence ADD_ON_ORDER = of("ADD");
    public static final ByteSequence ALL_OR_NONE = of("AON");
    public static final ByteSequence CASH_NOT_HELD = of("CNH");
    public static final ByteSequence DIRECTED_ORDER = of("DIR");
    public static final ByteSequence EXCHANGE_FOR_PHYSICAL_TRANSACTION = of("E.W");
    public static final ByteSequence FILL_OR_KILL = of("FOK");
    public static final ByteSequence IMMEDIATE_OR_CANCEL = of("IOC");
    public static final ByteSequence LIMIT_ON_CLOSE = of("LOC");
    public static final ByteSequence LIMIT_ON_OPEN = of("LOO");
    public static final ByteSequence MARKET_AT_CLOSE = of("MAC");
    public static final ByteSequence MARKET_AT_OPEN = of("MAO");
    public static final ByteSequence MARKET_ON_CLOSE = of("MOC");
    public static final ByteSequence MARKET_ON_OPEN = of("MOO");
    public static final ByteSequence MINIMUM_QUANTITY = of("MQT");
    public static final ByteSequence OVER_THE_DAY = of("OVD");
    public static final ByteSequence PEGGED = of("PEG");
    public static final ByteSequence RESERVE_SIZE_ORDER = of("RSV");
    public static final ByteSequence STOP_STOCK_TRANSACTION = of("S.W");
    public static final ByteSequence SCALE = of("SCL");
    public static final ByteSequence TIME_ORDER = of("TMO");
    public static final ByteSequence WORK = of("WRK");

}