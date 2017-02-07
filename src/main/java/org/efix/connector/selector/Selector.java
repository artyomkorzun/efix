package org.efix.connector.selector;

import org.efix.util.LangUtil;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Implements the common functionality for a selector poller.
 */
public class Selector implements AutoCloseable {

    private static final String SELECTOR_IMPL = "sun.nio.ch.SelectorImpl";

    protected static final Field SELECTED_KEYS_FIELD;
    protected static final Field PUBLIC_SELECTED_KEYS_FIELD;

    static {
        Field selectKeysField = null;
        Field publicSelectKeysField = null;

        try {
            final Class<?> clazz = Class.forName(SELECTOR_IMPL, false, ClassLoader.getSystemClassLoader());

            if (clazz.isAssignableFrom(java.nio.channels.Selector.open().getClass())) {
                selectKeysField = clazz.getDeclaredField("selectedKeys");
                selectKeysField.setAccessible(true);

                publicSelectKeysField = clazz.getDeclaredField("publicSelectedKeys");
                publicSelectKeysField.setAccessible(true);
            }
        } catch (final Exception ex) {
            LangUtil.rethrow(ex);
        } finally {
            SELECTED_KEYS_FIELD = selectKeysField;
            PUBLIC_SELECTED_KEYS_FIELD = publicSelectKeysField;
        }
    }

    protected final java.nio.channels.Selector selector;
    protected final NioSelectedKeySet selectedKeys;

    public Selector() {
        try {
            selector = java.nio.channels.Selector.open();
            selectedKeys = new NioSelectedKeySet();

            SELECTED_KEYS_FIELD.set(selector, selectedKeys);
            PUBLIC_SELECTED_KEYS_FIELD.set(selector, selectedKeys);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public java.nio.channels.Selector selector() {
        return selector;
    }

    public NioSelectedKeySet selectNow() {
        try {
            selectedKeys.reset();
            selector.selectNow();
            return selectedKeys;
        } catch (IOException e) {
            throw LangUtil.rethrow(e);
        }
    }

    /**
     * Clears cancelled channels.
     */
    public void cancel(){
        selectNow();
    }

    /**
     * Close NioSelector down. Returns immediately.
     */
    public void close() {
        selector.wakeup();
        try {
            selector.close();
        } catch (final IOException ex) {
            LangUtil.rethrow(ex);
        }
    }

}
