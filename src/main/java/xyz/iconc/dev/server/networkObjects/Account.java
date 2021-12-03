package xyz.iconc.dev.server.networkObjects;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import xyz.iconc.dev.server.utilities.Utility;

import java.io.Serializable;

public class Account implements Serializable {
    private final long userIdentifier;
    private final String username;
    private final String hashedPassword;

    private Object salt;
    private final long dateRegistered;
    private final int accountType;

    /**
     * DO NOT USE TO CONSTRUCT EXISTING ACCOUNT
     * Intended only to create new accounts.
     *
     * @param _username
     * @param _password
     */
    public Account(String _username, String _password) {
        Account account = InitializeNewAccount(_username, _password);
        this.userIdentifier = account.userIdentifier;
        this.username = account.username;
        this.hashedPassword = account.hashedPassword;
        this.dateRegistered = account.dateRegistered;
        this.accountType = account.accountType;
    }

    /**
     * Construct an account that already exists
     *
     * @param _identifier
     * @param _username
     * @param _password
     * @param _dateRegistered
     * @param _accountType
     */
    public Account(long _identifier, String _username, String _password, Object _salt,
                   long _dateRegistered, int _accountType) {
        userIdentifier = _identifier;
        username = _username;
        hashedPassword = _password;
        salt = _salt;
        dateRegistered = _dateRegistered;
        accountType = _accountType;
    }




    private static Account InitializeNewAccount(String _username, String _password) {
        UUID uuid = new UUID(NetworkObjectType.ACCOUNT);

        Object salt = generateSalt();
        String hashedPasswordBase64 = new Sha512Hash(_password, salt, 1024).toBase64();

        return new Account(uuid.getIdentifier(), _username, hashedPasswordBase64, salt,
                uuid.getEpochTime(), 0);
    }

    private static Object generateSalt() {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        return rng.nextBytes();
    }


    public long getUserIdentifier() {
        return userIdentifier;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Object getSalt() {
        return salt;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public int getAccountType() {
        return accountType;
    }

}
