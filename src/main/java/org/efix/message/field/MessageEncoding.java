package org.efix.message.field;

import org.efix.util.ByteSequence;
import org.efix.util.ByteSequenceWrapper;


public class MessageEncoding {

    public static final ByteSequence UTF_8 = ByteSequenceWrapper.of("UTF-8");
    public static final ByteSequence EUC_JP = ByteSequenceWrapper.of("EUC-JP");
    public static final ByteSequence SHIFT_JIS = ByteSequenceWrapper.of("SHIFT_JIS");
    public static final ByteSequence ISO_2022_JP = ByteSequenceWrapper.of("ISO-2022-JP");

}