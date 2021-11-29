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
    private final String databaseName;

    public DatabaseManager(boolean debug) {
        super();
        readyState = new AtomicBoolean(false);

        Configuration cfg;
        if (!debug) {
            cfg = Server.getConfig();
        } else {
            cfg = new Configuration();
        }

        databaseName = cfg.getConfigValue(Configuration.ConfigOptions.DATABASE_NAME);


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
     * Creates a new account in the database
     *
     * @param username username of the user requested.
     * @param hashedPassword hashed & salted password of the account
     * @return true if operation successful
     */
    public boolean insert_createAccount(String username, String hashedPassword) {
        if (!readyState.get()) return false;

        String query = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?)";

        UUID uuid = new UUID(NetworkObjectType.ACCOUNT);

        Account newAccount = new Account(username, hashedPassword);


        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            //statement.setString(1, databaseName);

            statement.setLong(1, newAccount.getUserIdentifier());


            statement.setString(2, username);


            statement.setString(3, hashedPassword);

            statement.setLong(4, newAccount.getDateRegistered());


            statement.setInt(5, newAccount.getAccountType());

            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     *  Creates a new channel in the database
     *
     * @return true if operation successful
     */
    public boolean insert_createChannel() {
        String sql = "INSERT INTO channels VALUES (?,?)";

        Channel channel = Channel.CreateChannel();

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, channel.getChannelIdentifier());
            statement.setLong(2, channel.getCreationEpoch());

            statement.execute();
            statement.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean insert_createMessage(long messageIdentifier, long channelSentIdentifier,
                                        long channelAuthorIdentifier, String messageContents) {
        if (!isReady()) return false;


        String query = "INSERT INTO messages VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);


            statement.setLong(5, new UUID(messageIdentifier).getEpochTime());


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

        databaseManager.insert_createAccount("username", "password");
    }
}
