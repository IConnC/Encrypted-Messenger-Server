package xyz.iconc.dev.server.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.NoSuchAlgorithmException;

public class AuthenticationManager {
    public AuthenticationManager() {

    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        System.out.println(maxKeySize);

    }
}
