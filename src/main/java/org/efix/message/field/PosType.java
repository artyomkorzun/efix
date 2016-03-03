package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class PosType {

    public static final ByteSequence OPTION_ASSIGNMENT = ByteSequenceWrapper.of("AS");
    public static final ByteSequence DELIVERY_NOTICE_QTY = ByteSequenceWrapper.of("DN");
    public static final ByteSequence EXCHANGE_FOR_PHYSICAL_QTY = ByteSequenceWrapper.of("EP");
    public static final ByteSequence OPTION_EXERCISE_QTY = ByteSequenceWrapper.of("EX");
    public static final ByteSequence ADJUSTMENT_QTY = ByteSequenceWrapper.of("PA");
    public static final ByteSequence TRANSACTION_FROM_ASSIGNMENT = ByteSequenceWrapper.of("TA");
    public static final ByteSequence TRANSACTION_QUANTITY = ByteSequenceWrapper.of("TQ");
    public static final ByteSequence TRANSACTION_FROM_EXERCISE = ByteSequenceWrapper.of("TX");
    public static final ByteSequence CROSS_MARGIN_QTY = ByteSequenceWrapper.of("XM");
    public static final ByteSequence ALLOCATION_TRADE_QTY = ByteSequenceWrapper.of("ALC");
    public static final ByteSequence AS_OF_TRADE_QTY = ByteSequenceWrapper.of("ASF");
    public static final ByteSequence CORPORATE_ACTION_ADJUSTMENT = ByteSequenceWrapper.of("CAA");
    public static final ByteSequence DELIVERY_QTY = ByteSequenceWrapper.of("DLV");
    public static final ByteSequence ELECTRONIC_TRADE_QTY = ByteSequenceWrapper.of("ETR");
    public static final ByteSequence END_OF_DAY_QTY = ByteSequenceWrapper.of("FIN");
    public static final ByteSequence INTRA_SPREAD_QTY = ByteSequenceWrapper.of("IAS");
    public static final ByteSequence INTER_SPREAD_QTY = ByteSequenceWrapper.of("IES");
    public static final ByteSequence PIT_TRADE_QTY = ByteSequenceWrapper.of("PIT");
    public static final ByteSequence RECEIVE_QUANTITY = ByteSequenceWrapper.of("RCV");
    public static final ByteSequence START_OF_DAY_QTY = ByteSequenceWrapper.of("SOD");
    public static final ByteSequence INTEGRAL_SPLIT = ByteSequenceWrapper.of("SPL");
    public static final ByteSequence TOTAL_TRANSACTION_QTY = ByteSequenceWrapper.of("TOT");
    public static final ByteSequence TRANSFER_TRADE_QTY = ByteSequenceWrapper.of("TRF");

}