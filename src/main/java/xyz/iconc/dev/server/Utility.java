package xyz.iconc.dev.server;

import xyz.iconc.dev.server.objects.Message;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utility {
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
        SerializeObject(new Message());
    }
}
