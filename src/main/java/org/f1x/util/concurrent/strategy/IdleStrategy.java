package org.f1x.util.concurrent.strategy;

/**
 * Idle strategy for use by threads when they do not have work to do.
 * <p>
 * <h3>Note regarding implementor state</h3>
 * <p>
 * Some implementations are known to be stateful, please note that you cannot safely assume implementations to be stateless.
 * Where implementations are stateful it is recommended that implementation state is padded to avoid false sharing.
 * <p>
 * <h3>Note regarding potential for TTSP(Time To Safe Point) issues</h3>
 * <p>
 * If the caller spins in a 'counted' loop, and the implementation does not include a a safepoint poll this may cause a TTSP
 * (Time To SafePoint) problem. If this is the case for your application you can solve it by preventing the idle method from
 * being inlined by using a Hotspot compiler command as a JVM argument e.g:
 * <code>-XX:CompileCommand=dontinline,org.f1x.util.concurrent.strategy.NoOpIdleStrategy::idle</code>
 */
public interface IdleStrategy {

    /**
     * Perform current idle action (e.g. nothing/yield/sleep). This method signature expects users to call into it on every work
     * 'cycle'. The implementations may use the indication "workCount &gt; 0" to reset internal backoff state. This method works
     * well with 'work' APIs which follow the following rules:
     * <ul>
     * <li>'work' returns a value larger than 0 when some work has been done</li>
     * <li>'work' returns 0 when no work has been done</li>
     * <li>'work' may return error codes which are less than 0, but which amount to no work has been done</li>
     * </ul>
     *
     * Callers are expected to follow this pattern:
     *
     * <pre>
     * <code>
     * while (isRunning)
     * {
     *     idleStrategy.idle(doWork());
     * }
     * </code>
     * </pre>
     *
     * @param workCount performed in last duty cycle.
     */
    void idle(int workCount);

}
