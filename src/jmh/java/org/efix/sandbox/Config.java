package org.efix.sandbox;

import org.efix.util.buffer.UnsafeBuffer;
import org.openjdk.jmh.annotations.CompilerControl;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;


class Config {

    public static final String[] JVM_ARGS = {"-Defix.disable.bounds.check=true", /*"-XX:+PrintCompilation",*/ /*"-server",*/ "-XX:-TieredCompilation"};
    public static String message = "8=FIX.4.2|9=321|35=8|49=CIBC_CORE_TEST|56=DELTIX_TEST|34=53|52=20150429-16:13:33.146|57=CI001CI|55=MSFT|37=1tU1210_00Y|11=15|17=1tU1210_000134|20=0|150=2|39=2|58=Child Order Executed|54=1|40=1|59=0|21=1|38=100|151=0|14=100|31=100|32=100|6=100|109=DLT_TRDR_TEST|60=20150429-16:13:33.117|15=USD|7201=CL|7207=N|30=NYSE|421=US|9730=R|25004=N|10=210|".replace('|', '\u0001');
    public static byte[] array = message.getBytes(StandardCharsets.US_ASCII);
    public static UnsafeBuffer buffer;

    public static int offset = 0;
    public static int length = array.length;

    public static int integer = Integer.MAX_VALUE;

    static {
        UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(array.length));

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.putByte(i, array[i]);
        }

        Config.buffer = buffer;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static int randomInt() {
        return /*integer;*/ ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

}
