package xyz.iconc.dev.server;

import xyz.iconc.dev.objects.*;
import xyz.iconc.dev.server.objects.IReady;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    }

    public void start(){
        initializeDatabaseConnection();
        //connection.getAutoCommit();
        readyState.set(true);
    }


    public Message get_message(long identifier) {
        String sql = "SELECT * FROM messages WHERE message_identifier=?";

        Message message;

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, identifier);

            ResultSet rs = statement.executeQuery();

            rs.next();

            message = new Message(identifier, rs.getLong(3),
                    rs.getLong(2), rs.getString(4));

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return message;
    }

    public List<Message> get_messages(long channelIdentifier) {
        String sql = "SELECT * FROM messages WHERE channel_sent_id=?";

        List<Message> messages = new ArrayList<>();

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, channelIdentifier);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Message tempMessage = new Message(rs.getLong(1),
                        rs.getLong(3), channelIdentifier, rs.getString(4));
                messages.add(tempMessage);
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return messages;
    }

    /**
     *  Overload for get_message(long identifier)
     * @param uuid UUID of the message to get from the database
     * @return Returns Message if successful or null if not
     */
    public Message get_message(UUID uuid) {
        return get_message(uuid.getIdentifier());
    }

    public List<User> get_channelMembers(long channelIdentifier) {
        String sql = "SELECT * FROM channel_members WHERE channel_identifier=?";

        List<User> channelMembers = new ArrayList<>();

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, channelIdentifier);

            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                channelMembers.add(new User(rs.getLong(2)));
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return channelMembers;
    }

    public List<Channel> getSubscribedChannels(long userIdentifier) {
        String getChannelsSQL = "SELECT channel_identifier FROM channel_members WHERE user_identifier=?";
        String getChannelInfoSQL = "SELECT channel_name FROM channels WHERE channel_identifier=?";

        List<Channel> channels = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(getChannelsSQL);
            stmt.setLong(1, userIdentifier);
            stmt.execute();

            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                PreparedStatement ps = connection.prepareStatement(getChannelInfoSQL);
                ps.setLong(1, rs.getLong(1));
                ps.execute();
                ResultSet rs2 = ps.getResultSet();
                rs2.next();

                channels.add(new Channel(rs.getLong(1), rs2.getString(1)));
                ps.close();
                rs2.next();
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return channels;
    }

    public List<Channel> get_channels() {
        String sql = "SELECT channel_identifier,channel_name FROM channels";

        List<Channel> channels = new ArrayList<>();

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);


            ResultSet rs = statement.executeQuery();


            while (rs.next()) {
                channels.add(new Channel(rs.getLong(1), rs.getString(2)));
            }

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return channels;
    }

    public List<User> get_accounts() {
        String sql = "SELECT * FROM accounts";
        List<User> users = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                users.add(new User(rs.getLong(1)));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return users;
    }

    public User get_account(long identifier) {
        String sql = "SELECT * FROM accounts WHERE user_identifier=?";

        User user = null;

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            statement.setLong(1, identifier);

            ResultSet rs = statement.executeQuery();

            rs.next();

            List<Channel> subscribedChannels = getSubscribedChannels(identifier);
            Channel[] subscribedChannelsArray = new Channel[subscribedChannels.size()];
            for (int i=0; i<subscribedChannels.size(); i++) {
                subscribedChannelsArray[i] = subscribedChannels.get(i);
            }

            user = new User(rs.getLong(1), rs.getString(2),
                    rs.getString(3), rs.getLong(4),
                    rs.getLong(5), subscribedChannelsArray);

            rs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public void update_account(User user) {
        if (!user.isPopulated()) return;

        String sql = "UPDATE accounts SET username = ?, hashed_password=?, epoch_registered=?," +
                " last_message_received_epoch = ? WHERE user_identifier=?;";

        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getHashedPassword());
            stmt.setLong(3, user.getDateRegistered());
            stmt.setLong(4, user.getLastMessageReceivedEpoch());
            stmt.setLong(5, user.getUserIdentifier());

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean update_account_last_message_received_epoch(long userIdentifier, long epoch) {
        String sql = "UPDATE accounts SET last_message_received_epoch = ? WHERE user_identifier=?;";

        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement(sql);

            stmt.setLong(1, epoch);
            stmt.setLong(2, userIdentifier);
            stmt.execute();
            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Inserts a given account object into the database.
     *
     * @param user the account to insert into the database
     * @return True if successful
     */
    public boolean insert_account(User user) {
        if (!readyState.get()) return false;

        String query = "INSERT INTO accounts VALUES (?, ?, ?, ?, ?)";



        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);

            statement.setLong(1, user.getUserIdentifier());

            statement.setString(2, user.getUsername());

            statement.setString(3, user.getHashedPassword());

            statement.setLong(4, user.getDateRegistered());

            statement.setLong(5, user.getLastMessageReceivedEpoch());

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

        String sql = "UPDATE channels SET channel_name=? WHERE channel_identifier=?;";

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

        String sql = "DELETE FROM channel_members WHERE user_identifier=? AND channel_identifier=?";

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

        System.out.println(databaseManager.get_message(123));
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
