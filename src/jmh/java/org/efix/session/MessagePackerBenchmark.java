package org.efix.session;

import org.efix.FixVersion;
import org.efix.SessionId;
import org.efix.message.field.MsgType;
import org.efix.util.BenchmarkUtil;
import org.efix.util.buffer.Buffer;
import org.efix.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 5)
public class MessagePackerBenchmark {

    private static final long NOW = System.currentTimeMillis();
    private final SessionId sessionId = new SessionId("Sender", "Receiver");
    private final MessagePacker packer = new MessagePacker(new UnsafeBuffer(new byte[1024]));
    private final Buffer buffer = BenchmarkUtil.makeMessage("1=account|11=1|38=1000|40=2|44=1.2345|54=1|55=EUR/USD|59=0|60=20170220-09:15:33.704|76=cfh|100=#INSTANT-FILL|167=FOR");

    @Benchmark
    public void pack() {
        packer.pack(FixVersion.FIX44, sessionId, 1000, NOW, MsgType.ORDER_SINGLE, buffer, 0, buffer.capacity());
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MessagePackerBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
