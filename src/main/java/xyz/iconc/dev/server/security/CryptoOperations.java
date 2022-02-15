package xyz.iconc.dev.server.security;

import org.bouncycastle.jcajce.provider.symmetric.AES;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Base64;

public class CryptoOperations {

    public static void main(String[] args) throws Exception {
        SecretKey privateKey = generateKey();
        byte[] encrypted = encryptAES512("TEST INPUT", privateKey);
        //System.out.println(Base64.getEncoder().encodeToString(encrypted));
        System.out.println(new String(Base64.getDecoder().decode(decryptAES512(encrypted, privateKey))));
    }

    public static SecretKey generateKey() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        return KeyGenerator.getInstance("AES", "BC").generateKey();
    }

    public static byte[] encryptAES512(String input, Key key) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES", "BC");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(Base64.getEncoder().encode(input.getBytes(StandardCharsets.UTF_8)));

    }

    public static byte[] decryptAES512(byte[] encrypted, Key key) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(encrypted);
    }
}
