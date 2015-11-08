package org.f1x.util;

public final class Registry {

    public static final String CHECK_BOUNDS_ENTRY_KEY = "f1x.checkBounds";
    public static final String CACHE_LINE_LENGTH_ENTRY_KEY = "f1x.cacheLineLength";

    private Registry() {
        throw new AssertionError("Not for you, man!");
    }

    public static int getIntValue(String key) {
        String value = getStringValue(key);
        return Integer.parseInt(value);
    }

    public static int getIntValue(String key, int defaultValue) {
        String value = getStringValue(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    public static long getLongValue(String key) {
        String value = getStringValue(key);
        return Long.parseLong(value);
    }

    public static long getLongValue(String key, long defaultValue) {
        String value = getStringValue(key);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    public static boolean getBooleanValue(String key) {
        String value = getStringValue(key);
        return parseBoolean(value);
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        String value = getStringValue(key, null);
        return value == null ? defaultValue : parseBoolean(value);
    }

    public static String getStringValue(String key) {
        String value = System.getProperty(key);
        return Checker.checkNotNull(value);
    }

    public static String getStringValue(String key, String defaultValue) {
        String value = System.getProperty(key);
        return value == null ? defaultValue : value;
    }

    private static boolean parseBoolean(String value) {
        switch (value) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new IllegalArgumentException("Invalid boolean value: " + value);
        }
    }

}
