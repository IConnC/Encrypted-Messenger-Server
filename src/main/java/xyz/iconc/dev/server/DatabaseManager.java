package xyz.iconc.dev.server;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xyz.iconc.dev.server.networkObjects.*;
import xyz.iconc.dev.server.objects.IReady;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

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
        //connection.getAutoCommit();
        readyState.set(true);

    }

    private void createFreshDatabase() {
        throw new NotImplementedException();
    }


    /**
     * Inserts a given account object into the database.
     *
     * @param account the account to insert into the database
     * @return True if successful
     */
    public boolean insert_account(Account account) {
        if (!readyState.get()) return false;

        String query = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?, ?)";



        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            statement.setLong(1, account.getUserIdentifier());

            statement.setString(2, account.getUsername());

            statement.setString(3, account.getHashedPassword());

            statement.setString(4, (String) account.getSalt());

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

    public boolean insert_channelMember(long channelIdentifier, long userIdentifier) {

        String sql = "INSERT INTO channel_members VALUES (?,?)";


        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, channelIdentifier);
            statement.setLong(2, userIdentifier);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     *  Inserts a new message into the database
     *
     * @param message The message object to insert
     * @return True if operation is successful
     */
    public boolean insert_message(Message message) {
        if (!isReady()) return false;


        String query = "INSERT INTO messages VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            statement.setLong(1, message.getMessageIdentifier());
            statement.setLong(2, message.getChannelIdentifier());
            statement.setLong(3, message.getSenderIdentifier());
            statement.setString(4, message.getMessageContents());
            statement.setLong(5, new UUID(message.getMessageIdentifier()).getEpochTime());

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
        readyState.set(false);

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

    /**
     * <p>Attempts to establish a connection with the data source that
     * this {@code DataSource} object represents.
     *
     * @return a connection to the data source
     * @throws SQLException        if a database access error occurs
     * @throws SQLTimeoutException when the driver has determined that the
     *                             timeout value specified by the {@code setLoginTimeout} method
     *                             has been exceeded and has at least tried to cancel the
     *                             current database connection attempt
     */
    public Connection getConnection() {
        if (!isReady()) return null;
        if (connection != null) {
            return connection;
        }
        return null;
    }

}
