package org.f1x.util;

import org.f1x.util.buffer.Buffer;
import org.f1x.util.buffer.UnsafeBuffer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    protected static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    protected static final DateTimeFormatter TIMESTAMP_PARSER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss[.SSS]");
    protected static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss.SSS");
    protected static final DateTimeFormatter DATE_PARSER_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");
    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static long parseTimestamp(String string) {
        LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_PARSER);
        return timestamp.atZone(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static int parseTime(String string) {
        LocalTime time = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        return (int) (time.toNanoOfDay() / 1000000);
    }

    public static long parseDate(String string) {
        LocalDate date = LocalDate.parse(string, DATE_PARSER_FORMATTER);
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli();
    }


    public static String formatTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(TIMESTAMP_FORMATTER);
    }

    public static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(DATE_PARSER_FORMATTER);
    }

    public static String formatTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(TIME_FORMATTER);
    }


    public static UnsafeBuffer byteMessage(String message) {
        message = message.replace('|', '\u0001');
        return BufferUtil.fromString(message);
    }

    public static String stringMessage(Buffer buffer, int offset, int length) {
        return BufferUtil.toString(buffer, offset, length).replace('\u0001', '|');
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

    public static double generateDouble(double from, double to) {
        return ThreadLocalRandom.current().nextDouble(from, Math.nextUp(to));
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... objects) {
        return objects;
    }

}
