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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    public static final DateFormat UTC_TIMESTAMP_FORMAT = createUTCDateFormat(TimestampFormatter.DATE_TIME_FORMAT);
    public static final DateFormat UTC_TIME_FORMAT = createUTCDateFormat(TimeOfDayFormatter.FORMAT);
    public static final DateFormat LOCAL_DATE_FORMAT = createLocalDateFormat(TimestampFormatter.DATE_ONLY_FORMAT);

    public static long parseUTCTimestamp(String timestamp) {
        return parseDateFormat(timestamp, UTC_TIMESTAMP_FORMAT);
    }

    public static int parseUTCTime(String string) {
        LocalTime time = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        return (int) (time.toNanoOfDay() / 1000000);
    }

    public static long parseUTCDate(String string) {
        LocalDate date = LocalDate.parse(string, DateTimeFormatter.BASIC_ISO_DATE);
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli();
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

    public static int generateInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

}
