package org.efix;

public class SampleConfiguration {

    static final int WARMUP_REPEATS = Integer.getInteger("warmup.repeats", 5);
    static final int WARMUP_ITERATIONS = Integer.getInteger("warmup.iterations", 50000);

    static final int MEASURE_REPEATS = Integer.getInteger("measure.repeats", 5);
    static final int MEASURE_ITERATIONS = Integer.getInteger("measure.iterations", 1000000);

    static final String HOST = System.getProperty("host", "localhost");
    static final int PORT = Integer.getInteger("port", 10000);


}
