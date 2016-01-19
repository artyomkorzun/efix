package org.f1x.message.field;

import org.f1x.util.ByteSequence;

import static org.f1x.util.ByteSequenceWrapper.of;


public class MessageEncoding {

    public static final ByteSequence UTF_8 = of("UTF-8");
    public static final ByteSequence EUC_JP = of("EUC-JP");
    public static final ByteSequence SHIFT_JIS = of("SHIFT_JIS");
    public static final ByteSequence ISO_2022_JP = of("ISO-2022-JP");

}