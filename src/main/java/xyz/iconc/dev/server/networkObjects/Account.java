package xyz.iconc.dev.server.networkObjects;

import xyz.iconc.dev.server.utilities.Utility;

import java.io.Serializable;

public class Account implements Serializable {
    private final long userIdentifier;
    private final String username;
    private final String hashedPassword;
    private final long dateRegistered;
    private final int accountType;

    /**
     * DO NOT USE TO CONSTRUCT EXISTING ACCOUNT
     * Meant only to create new accounts.
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
    public Account(long _identifier, String _username, String _password,
                   long _dateRegistered, int _accountType) {
        userIdentifier = _identifier;
        username = _username;
        hashedPassword = _password;
        dateRegistered = _dateRegistered;
        accountType = _accountType;
    }




    public static Account InitializeNewAccount(String _username, String _password) {
        UUID uuid = new UUID(NetworkObjectType.ACCOUNT);
        return new Account(uuid.getIdentifier(), _username, _password,
                uuid.getEpochTime(), 0);
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

    public long getDateRegistered() {
        return dateRegistered;
    }

    public int getAccountType() {
        return accountType;
    }

}
