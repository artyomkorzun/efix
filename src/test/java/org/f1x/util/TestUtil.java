package org.f1x.util;

import org.f1x.util.buffer.UnsafeBuffer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    protected static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    protected static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss[.SSS]");
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");


    public static long parseTimestamp(String string) {
        LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_FORMATTER);
        return timestamp.atZone(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static int parseTime(String string) {
        LocalTime time = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        return (int) (time.toNanoOfDay() / 1000000);
    }

    public static long parseDate(String string) {
        LocalDate date = LocalDate.parse(string, DATE_FORMATTER);
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli();
    }


    public static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(DATE_FORMATTER);
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

    public static long generateLong(long from, long to) {
        return ThreadLocalRandom.current().nextLong(from, to + 1);
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... objects) {
        return objects;
    }

}
