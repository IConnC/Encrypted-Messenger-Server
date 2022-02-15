package xyz.iconc.dev.server.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.server.Server;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

public class Utility {


    public static long GetUnixEpoch() {
        return Instant.now().toEpochMilli() - Server.UNIX_EPOCH_MILLISECONDS_START;
    }


    /**
     * Initializes logger for class.
     * @param cls The Class that needs a logger instance
     * @return Logger ready to be used
     */
    public static Logger GetLogger(Class cls) {
        return LoggerFactory.getLogger(cls);
    }

    public static boolean[] getBitsFromLong(long num) {
        return null;
    }

    // Returns the binary of a long number
    public static byte[] getBinary(long num) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE)
                .putLong(num)
                .array();
    }


    public static byte convertStringToByte(String str)
    {
        return Byte.parseByte(str);
    }

    public static void SerializeObject(Object obj) {
        System.out.println(Arrays.toString(obj.getClass().getDeclaredFields()));
    }

    public static void main(String[] args) {
        //SerializeObject(new Message());
        getBitsFromLong(1L);
        getBitsFromLong(2L);
        getBitsFromLong(64L);
        getBitsFromLong(63L);


    }
}
