package org.f1x.util;

import junit.framework.AssertionFailedError;
import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;
import org.f1x.util.format.TimeOfDayFormatter;
import org.f1x.util.format.TimestampFormatter;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class TestUtil {

    public static DateFormat UTC_TIMESTAMP_FORMAT = createUTCDateFormat(TimestampFormatter.DATE_TIME_FORMAT);
    public static DateFormat UTC_TIME_FORMAT = createUTCDateFormat(TimeOfDayFormatter.FORMAT);
    public static DateFormat UTC_DATE_FORMAT = createUTCDateFormat(TimestampFormatter.DATE_ONLY_FORMAT);
    public static DateFormat LOCAL_DATE_FORMAT = createLocalDateFormat(TimestampFormatter.DATE_ONLY_FORMAT);

    public static long parseUTCTimestamp(String timestamp) {
        return parseDateFormat(timestamp, UTC_TIMESTAMP_FORMAT);
    }

    public static long parseUTCTime(String time) {
        return parseDateFormat(time, UTC_TIME_FORMAT);
    }

    public static long parseUTCDate(String date) {
        return parseDateFormat(date, UTC_DATE_FORMAT);
    }

    public static long parseLocalDate(String date) {
        return parseDateFormat(date, LOCAL_DATE_FORMAT);
    }

    public static long parseDateFormat(String string, DateFormat format) {
        try {
            return format.parse(string).getTime();
        } catch (ParseException e) {
            throw new AssertionFailedError("Error parsing string: " + string + ": " + e.getMessage());
        }
    }

    protected static DateFormat createUTCDateFormat(String format) {
        return createDateFormat(format, TimeZone.getTimeZone("UTC"));
    }

    protected static DateFormat createLocalDateFormat(String format) {
        return createDateFormat(format, TimeZone.getDefault());
    }

    protected static DateFormat createDateFormat(String format, TimeZone timeZone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.US));
        return dateFormat;
    }

    public static Buffer makeMessage(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

}
