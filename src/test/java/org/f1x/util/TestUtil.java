package org.f1x.util;

import org.f1x.util.buffer.UnsafeBuffer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss[.SSS]");

    public static long parseTimestamp(String string) {
        LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_FORMATTER);
        return timestamp.atZone(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static int parseTime(String string) {
        LocalTime time = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        return (int) (time.toNanoOfDay() / 1000000);
    }

    public static long parseDate(String string) {
        LocalDate date = LocalDate.parse(string, DateTimeFormatter.BASIC_ISO_DATE);
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static UnsafeBuffer makeMessage(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

    public static int generateInt(int from, int to) {
        return (int) ThreadLocalRandom.current().nextLong(from, to + 1L);
    }

    public static int generateInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static long generateLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... objects) {
        return objects;
    }

}
