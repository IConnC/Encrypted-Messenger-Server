package xyz.iconc.dev.server.storage;

import xyz.iconc.dev.server.Configuration;
import xyz.iconc.dev.server.Server;
import xyz.iconc.dev.server.networkObjects.Account;
import xyz.iconc.dev.server.networkObjects.Channel;
import xyz.iconc.dev.server.networkObjects.NetworkObjectType;
import xyz.iconc.dev.server.objects.IReady;
import xyz.iconc.dev.server.networkObjects.UUID;

import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseManager implements IReady {
    private final AtomicBoolean readyState;
    private final String connectionUrl;
    private Connection connection;

    public DatabaseManager(boolean debug) {
        super();
        readyState = new AtomicBoolean(false);

        Configuration cfg;
        if (!debug) {
            cfg = Server.getConfig();
        } else {
            cfg = new Configuration();
        }

        String databaseName = cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_NAME);


        // Generating Connection URI for MariaDB server from config.
        String tempUrl = "jdbc:mariadb://";
        tempUrl += cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_HOST) + ":";
        tempUrl += cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_PORT) + "/";
        tempUrl += databaseName + "?user=";
        tempUrl += cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_USER) + "&password=";
        tempUrl += cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_PASSWORD);

        connectionUrl = tempUrl;
        //DATABASE_CONNECTION_STRING("jdbc:mariadb://[host][,failoverhost][:port]/[database]"),



        initializeDatabaseConnection();
        readyState.set(true);

    }


    /**
     * Inserts a given account object into the database.
     *
     * @param account the account to insert into the database
     * @return True if successful
     */
    public boolean insert_account(Account account) {
        if (!readyState.get()) return false;

        String query = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?)";



        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            statement.setLong(1, account.getUserIdentifier());

            statement.setString(2, account.getUsername());

            statement.setString(3, account.getHashedPassword());

            statement.setLong(4, account.getDateRegistered());

            statement.setInt(5, account.getAccountType());

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * Creates a new account in the database
     *
     * @param username username of the user requested.
     * @param hashedPassword hashed & salted password of the account
     * @return true if operation successful
     */
    public boolean insert_createAccount(String username, String hashedPassword) {

        Account newAccount = new Account(username, hashedPassword);
        return insert_account(newAccount);
    }


    /**
     * Inserts given channel object into database.
     *
     * @param channel channel to insert into the database
     * @return True if successful
     */
    public boolean insert_channel(Channel channel) {
        String sql = "INSERT INTO channels VALUES (?,?,?)";


        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, channel.getChannelIdentifier());
            statement.setString(2, channel.getChannelName());
            statement.setLong(3, channel.getCreationEpoch());

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     *  Creates a new blank channel.
     *
     * @return true if operation successful
     */
    public boolean insert_createChannel(String channelName) {

        Channel channel = Channel.CreateChannel(channelName);

        return insert_channel(channel);
    }




    /**
     *  Inserts a new message into the database
     *
     * @param messageIdentifier The messages identifier
     * @param channelSentIdentifier The channel's identifier in which the message is sent
     * @param channelAuthorIdentifier The author of the message identifier
     * @param messageContents The encrypted contents of the message
     * @return True if operation is successful
     */
    public boolean insert_createMessage(long messageIdentifier, long channelSentIdentifier,
                                        long channelAuthorIdentifier, String messageContents) {
        if (!isReady()) return false;


        String query = "INSERT INTO messages VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            statement.setLong(1, messageIdentifier);
            statement.setLong(2, channelSentIdentifier);
            statement.setLong(3, channelAuthorIdentifier);
            statement.setLong(4, messageIdentifier);
            statement.setLong(5, new UUID(messageIdentifier).getEpochTime());

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Updates a channels name according to the new provided name.
     *
     * @param channelIdentifier The Channel Identifier
     * @param channelName New Channel name
     * @return True if successful
     */
    public boolean update_channelName(long channelIdentifier, String channelName) {
        if (!isReady()) return false;

        String sql = "UPDATE channels SET channel_name=? WHERE channel_id=?;";

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, channelName);

            statement.setLong(2, channelIdentifier);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }



    private boolean delete_fromIdentifier(String tableName, String identifierPrefix, long identifier) {
        if (!isReady()) return false;

        String sql = "DELETE FROM ? WHERE ?=?";

        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(sql);

            statement.setString(1, tableName);
            statement.setString(2, identifierPrefix + "_identifier");
            statement.setLong(3, identifier);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * Deletes the given account from the database
     *
     * @param userIdentifier The identifier of the user to delete
     * @return True if successful
     */
    public boolean delete_account(long userIdentifier) {
        UUID uuid = new UUID(userIdentifier);
        if (uuid.getNetworkObjectType() != NetworkObjectType.ACCOUNT) return false;

        return delete_fromIdentifier("accounts", "user", userIdentifier);
    }

    /**
     * Deletes the given channel from the database.
     *
     * @param channelIdentifier The identifier of the channel to delete
     * @return True if successful
     */
    public boolean delete_channel(long channelIdentifier) {
        UUID uuid = new UUID(channelIdentifier);
        if (uuid.getNetworkObjectType() != NetworkObjectType.CHANNEL) return false;

        return delete_fromIdentifier("messages", "message", channelIdentifier);
    }


    /**
     * Deletes the given message from the messages table.
     *
     * @param messageIdentifier Identifier of the message to delete
     * @return True if successful
     */
    public boolean delete_message(long messageIdentifier) {
        UUID uuid = new UUID(messageIdentifier);
        if (uuid.getNetworkObjectType() != NetworkObjectType.MESSAGE) return false;

        return delete_fromIdentifier("messages", "message", messageIdentifier);

    }

    /**
     * Removes the member from the channel
     *
     * @param userIdentifier identifier of the user to remove from the channel
     * @param channelIdentifier identifier of the channel to remove the user from
     * @return True if successful
     */
    public boolean delete_channelMember(long userIdentifier, long channelIdentifier) {
        UUID uuid = new UUID(userIdentifier);
        if (uuid.getNetworkObjectType() != NetworkObjectType.ACCOUNT) return false;

        if (!isReady()) return false;

        String sql = "DELETE FROM channel_members WHERE userIdentifier=?, channelIdentfifier=?";

        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, userIdentifier);
            statement.setLong(2, channelIdentifier);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }





    /**
     * Initializes the Database connection and stores it as the variable connection.
     */
    private void initializeDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







    public void shutdown() {
        try {
            if (connection.isClosed()) {
                return;
            }
            connection.commit();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            connection = null;
        }
    }




    /**
     *
     * @return boolean  false if connection is not established and true if connection is established
     */
    @Override
    public boolean isReady() {
        return readyState.get();
    }




    public static void main(String[] args) throws InterruptedException {
        DatabaseManager databaseManager = new DatabaseManager(true);

        databaseManager.update_channelName(6969L, "NEW CHANNEL NAME");
        //databaseManager.insert_createAccount("username", "password");
        //databaseManager.insert_createChannel("test");

    }
}
