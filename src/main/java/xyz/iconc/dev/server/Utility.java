package xyz.iconc.dev.server;

import xyz.iconc.dev.server.objects.Message;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utility {


    public static boolean[] getBitsFromLong(long num) {
        return null;
    }

    // Returns the binary of a long number
    public static byte[] getBinary(long num) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE)
                .putLong(num)
                .array();
    }


    public static String GetMD5(String input) {
        byte[] digest;
        try {
            byte[] messageBytes = input.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            digest = md.digest(messageBytes);

        } catch (Exception e) {
            return "";
        }
        return "";
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
