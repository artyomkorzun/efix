package org.efix.log.filter;

import org.efix.util.buffer.Buffer;

@FunctionalInterface
public interface Filter {

    Filter ALL_PASS = (inbound, time, message, offset, length) -> false;

    /**
     * @return true if message is filtered.
     */
    boolean filter(boolean inbound, long time, Buffer message, int offset, int length);

}
