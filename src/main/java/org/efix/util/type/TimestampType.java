package org.efix.util.type;

public class TimestampType {

    public static final int SECOND_TIMESTAMP_LENGTH = DateType.LENGTH + 1 + TimeType.SECOND_TIME_LENGTH;
    public static final int MILLISECOND_TIMESTAMP_LENGTH = DateType.LENGTH + 1 + TimeType.MILLISECOND_TIME_LENGTH;

    public static final int YEAR_OFFSET = 0;
    public static final int MONTH_OFFSET = 4;
    public static final int DAY_OFFSET = 6;

    public static final int DASH_OFFSET = 8;
    public static final int TIME_OFFSET = DASH_OFFSET + 1;

    public static final int HOUR_OFFSET = TIME_OFFSET + 0;
    public static final int FIRST_COLON_OFFSET = TIME_OFFSET + 2;
    public static final int MINUTE_OFFSET = TIME_OFFSET + 3;
    public static final int SECOND_COLON_OFFSET = TIME_OFFSET + 5;
    public static final int SECOND_OFFSET = TIME_OFFSET + 6;
    public static final int DOT_OFFSET = TIME_OFFSET + 8;
    public static final int MILLISECOND_OFFSET = TIME_OFFSET + 9;

}
