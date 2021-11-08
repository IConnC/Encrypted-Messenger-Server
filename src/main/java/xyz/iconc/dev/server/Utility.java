package xyz.iconc.dev.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
