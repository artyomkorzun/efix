package org.efix.util;

import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.UnsafeBuffer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    protected static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    protected static final DateTimeFormatter TIMESTAMP_PARSER = new DateTimeFormatterBuilder().appendPattern("uuuuMMdd-HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();

    protected static final DateTimeFormatter TIMESTAMP_S_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss");
    protected static final DateTimeFormatter TIMESTAMP_MS_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss.SSS");
    protected static final DateTimeFormatter TIMESTAMP_US_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss.SSSSSS");
    protected static final DateTimeFormatter TIMESTAMP_NS_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss.SSSSSSSSS");

    protected static final DateTimeFormatter DATE_PARSER_FORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");
    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static long parseTimestampS(String string) {
        LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_PARSER);
        return timestamp.atZone(UTC_ZONE).toInstant().getEpochSecond();
    }

    public static long parseTimestampMs(String string) {
        LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_PARSER);
        return timestamp.atZone(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static long parseTimestampUs(String string) {
        final LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_PARSER);
        final Instant instant = timestamp.atZone(UTC_ZONE).toInstant();
        return instant.getEpochSecond() * 1_000_000 + instant.getNano() / 1000;
    }

    public static long parseTimestampNs(String string) {
        final LocalDateTime timestamp = LocalDateTime.parse(string, TIMESTAMP_PARSER);
        final Instant instant = timestamp.atZone(UTC_ZONE).toInstant();
        return instant.getEpochSecond() * 1_000_000_000 + instant.getNano();
    }

    public static int parseTime(String string) {
        LocalTime time = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME);
        return (int) (time.toNanoOfDay() / 1000000);
    }

    public static long parseDate(String string) {
        LocalDate date = LocalDate.parse(string, DATE_PARSER_FORMATTER);
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli();
    }

    public static String formatTimestampS(long timestampS) {
        return Instant.ofEpochSecond(timestampS).atZone(UTC_ZONE).format(TIMESTAMP_S_FORMATTER);
    }

    public static String formatTimestampMs(long timestampMs) {
        return Instant.ofEpochMilli(timestampMs).atZone(UTC_ZONE).format(TIMESTAMP_MS_FORMATTER);
    }

    public static String formatTimestampUs(long timestampUs) {
        return Instant.ofEpochSecond(0, timestampUs * 1_000).atZone(UTC_ZONE).format(TIMESTAMP_US_FORMATTER);
    }

    public static String formatTimestampNs(long timestamp) {
        return Instant.ofEpochSecond(0, timestamp).atZone(UTC_ZONE).format(TIMESTAMP_NS_FORMATTER);
    }

    public static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(DATE_PARSER_FORMATTER);
    }

    public static String formatTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(UTC_ZONE).format(TIME_FORMATTER);
    }


    public static UnsafeBuffer byteMessage(String message) {
        return BufferUtil.fromString(stringMessage(message));
    }

    public static String stringMessage(String message) {
        return message.replace('|', '\u0001');
    }

    public static String[] stringMessages(String... messages) {
        String[] msgs = new String[messages.length];
        for (int i = 0; i < messages.length; i++)
            msgs[i] = stringMessage(messages[i]);

        return msgs;
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

    public static String generateDecimal(int integerPartLength, int fractionalPartLength) {
        StringBuilder builder = new StringBuilder();
        if (generateInt(0, 99) < 50)
            builder.append('-');

        for (int i = 0; i < integerPartLength; i++)
            builder.append(generateInt(0, 9));

        builder.append('.');
        for (int i = 0; i < fractionalPartLength; i++)
            builder.append(generateInt(0, 9));

        return builder.toString();
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... objects) {
        return objects;
    }

    public static String concatenate(String[] strings) {
        String string = "";
        for (String s : strings)
            string += s;

        return string;
    }

}
