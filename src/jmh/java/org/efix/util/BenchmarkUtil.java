package org.efix.util;

import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.MutableBuffer;
import org.efix.util.buffer.UnsafeBuffer;

import java.util.concurrent.ThreadLocalRandom;

public class BenchmarkUtil {

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

    public static MutableBuffer makeMessage(String message) {
        message = message.replace('|', '\u0001');
        return new UnsafeBuffer(StringUtil.asciiBytes(message));
    }

}
