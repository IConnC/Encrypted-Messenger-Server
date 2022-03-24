package xyz.iconc.dev.objects;

import xyz.iconc.dev.server.DatabaseManager;
import xyz.iconc.dev.server.Server;

import java.io.Serializable;

public class User implements Serializable {
    private final long userIdentifier;
    private String username;
    private String hashedPassword;

    private Object salt;
    private long dateRegistered;
    private int accountType;

    private long lastMessageReceivedEpoch = 0L;

    /**
     * DO NOT USE TO CONSTRUCT EXISTING ACCOUNT
     * Intended only to create new accounts.
     *
     * @param _username
     * @param _password
     */
    public User(String _username, String _password) {
        User user = InitializeNewAccount(_username, _password);
        this.userIdentifier = user.userIdentifier;
        this.username = user.username;
        this.hashedPassword = user.hashedPassword;
        this.dateRegistered = user.dateRegistered;
        this.accountType = user.accountType;
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
    public User(long _identifier, String _username, String _password, Object _salt,
                long _dateRegistered, int _accountType) {
        userIdentifier = _identifier;
        username = _username;
        hashedPassword = _password;
        salt = _salt;
        dateRegistered = _dateRegistered;
        accountType = _accountType;
    }

    public User(long userIdentifier) {
        this.userIdentifier = userIdentifier;
        username = "";
        hashedPassword = "";
        salt = "";
        dateRegistered = 0L;
        accountType = 0;
    }

    public void populateData() {
        DatabaseManager db = Server.getServerInstance().getDatabaseManager();

        User user = db.get_account(userIdentifier);
        username = user.getUsername();
        hashedPassword = user.getHashedPassword();
        salt = user.getSalt();
        dateRegistered = user.getDateRegistered();
        accountType = user.getAccountType();
    }


    /**
     *
     * @param _username Username of new account
     * @param _password Password of new account
     * @return Initialized Account with hashed password.
     */
    private static User InitializeNewAccount(String _username, String _password) {
        UUID uuid = new UUID(NetworkObjectType.ACCOUNT);

        Object salt = "generateSalt();";
        String hashedPasswordBase64 = "";//new Sha512Hash(_password, salt, 1024).toBase64();

        return new User(uuid.getIdentifier(), _username, hashedPasswordBase64, salt,
                uuid.getEpochTime(), 0);
    }


    /**
     * Generates and returns a randomized salt.
     * @return The random salt
     */
    private static Object generateSalt() {
        //RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        return "";//rng.nextBytes();
    }


    public long getUserIdentifier() {
        return userIdentifier;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return "hashedPassword";
    }

    public Object getSalt() {
        return "salt";
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public int getAccountType() {
        return accountType;
    }

    public long getLastMessageReadEpoch() {
        return lastMessageReceivedEpoch;
    }

    public void setLastMessageReadEpoch(long epoch) {
        lastMessageReceivedEpoch = epoch;
    }

}
