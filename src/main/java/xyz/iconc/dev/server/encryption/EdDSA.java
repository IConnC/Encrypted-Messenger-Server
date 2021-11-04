package xyz.iconc.dev.server.encryption;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

public class EdDSA {
    public static void main(String[] args) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EdDSA");
            KeyPair kp = kpg.generateKeyPair();

            byte[] msg = "test_string".getBytes(StandardCharsets.UTF_8);

            Signature sig = Signature.getInstance("EdDSA");
            sig.initSign(kp.getPrivate());
            sig.update(msg);
            byte[] s = sig.sign();

            String encodedString = Base64.getEncoder().encodeToString(s);
            System.out.println(encodedString);

        } catch (Exception e) {
            System.out.println(e.toString());
        }



    }
}
