package xyz.iconc.dev.api;

import org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi;
import org.restlet.security.LocalVerifier;
import org.restlet.security.SecretVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerifier extends SecretVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVerifier.class);

    @Override
    public int verify(String identifier, char[] secret) {
        LOGGER.error(identifier);
        LOGGER.error(new String(secret));
        return RESULT_VALID;
    }
}
