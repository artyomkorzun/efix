package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class PosType {

    public static final ByteSequence OPTION_ASSIGNMENT = of("AS");
    public static final ByteSequence DELIVERY_NOTICE_QTY = of("DN");
    public static final ByteSequence EXCHANGE_FOR_PHYSICAL_QTY = of("EP");
    public static final ByteSequence OPTION_EXERCISE_QTY = of("EX");
    public static final ByteSequence ADJUSTMENT_QTY = of("PA");
    public static final ByteSequence TRANSACTION_FROM_ASSIGNMENT = of("TA");
    public static final ByteSequence TRANSACTION_QUANTITY = of("TQ");
    public static final ByteSequence TRANSACTION_FROM_EXERCISE = of("TX");
    public static final ByteSequence CROSS_MARGIN_QTY = of("XM");
    public static final ByteSequence ALLOCATION_TRADE_QTY = of("ALC");
    public static final ByteSequence AS_OF_TRADE_QTY = of("ASF");
    public static final ByteSequence CORPORATE_ACTION_ADJUSTMENT = of("CAA");
    public static final ByteSequence DELIVERY_QTY = of("DLV");
    public static final ByteSequence ELECTRONIC_TRADE_QTY = of("ETR");
    public static final ByteSequence END_OF_DAY_QTY = of("FIN");
    public static final ByteSequence INTRA_SPREAD_QTY = of("IAS");
    public static final ByteSequence INTER_SPREAD_QTY = of("IES");
    public static final ByteSequence PIT_TRADE_QTY = of("PIT");
    public static final ByteSequence RECEIVE_QUANTITY = of("RCV");
    public static final ByteSequence START_OF_DAY_QTY = of("SOD");
    public static final ByteSequence INTEGRAL_SPLIT = of("SPL");
    public static final ByteSequence TOTAL_TRANSACTION_QTY = of("TOT");
    public static final ByteSequence TRANSFER_TRADE_QTY = of("TRF");

}