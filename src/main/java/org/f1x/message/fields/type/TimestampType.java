package org.f1x.message.fields.type;

public class TimestampType {

    public static final int SECOND_TIMESTAMP_LENGTH = DateType.LENGTH + 1 + TimeType.SECOND_TIME_LENGTH;
    public static final int MILLISECOND_TIMESTAMP_LENGTH = DateType.LENGTH + 1 + TimeType.MILLISECOND_TIME_LENGTH;

    public static final int DASH_OFFSET = DateType.LENGTH;
    public static final int TIME_OFFSET = DASH_OFFSET + 1;

}
