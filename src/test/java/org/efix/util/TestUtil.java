package org.efix.util;

import org.efix.util.buffer.BufferUtil;
import org.efix.util.buffer.UnsafeBuffer;

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
