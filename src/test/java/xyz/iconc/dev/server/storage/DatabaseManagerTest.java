package xyz.iconc.dev.server.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.iconc.dev.server.DatabaseManager;
import xyz.iconc.dev.objects.User;
import xyz.iconc.dev.objects.Channel;
import xyz.iconc.dev.objects.Message;

class DatabaseManagerTest {

    DatabaseManager databaseManager;
    User user;
    Channel channel;
    Message message;

    public DatabaseManagerTest() {
        user = new User("TestAccount", "TestHashedPassword");
        channel = new Channel("Test Chanel");
        message = new Message(user.getUserIdentifier(), channel.getChannelIdentifier(), "Message Contents");
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        initializeNewConnection();
    }

    void initializeNewConnection() throws InterruptedException {
        databaseManager = new DatabaseManager(true);
        Thread waitForReadyThread = new Thread(() -> {
            try {
                while (!databaseManager.isReady()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitForReadyThread.start();

        waitForReadyThread.join();
    }


    @AfterEach
    void tearDown() {
        databaseManager.shutdown();
        databaseManager = null;
    }

    @Test
    void insert_account() {
        assert databaseManager.insert_account(user) : "Account Insert Statement Failed";

    }


    @Test
    void insert_channel() {
        assert databaseManager.insert_channel(channel) : "Channel Insert Statement Failed";
    }

    @Test
    void insert_message() {
        assert databaseManager.insert_message(message) : "Message Insert Statement Failed";
    }

    @Test
    void insert_channelMember() {
        assert databaseManager.insert_channelMember(channel.getChannelIdentifier(), user.getUserIdentifier());
    }

    @Test
    void update_channelName() {
        assert databaseManager.update_channelName(channel.getChannelIdentifier(), "New Channel Name") : "Channel Update Name Statement Failed";

    }

    @Test
    void delete_account() {
        assert databaseManager.delete_account(user.getUserIdentifier());
    }

    @Test
    void delete_channel() {
        assert databaseManager.delete_channel(channel.getChannelIdentifier());
    }

    @Test
    void delete_message() {
        assert databaseManager.delete_message(message.getMessageIdentifier());
    }

    @Test
    void delete_channelMember() {
        assert databaseManager.delete_channelMember(user.getUserIdentifier(), channel.getChannelIdentifier());
    }

    @Test
    void shutdown() {
        databaseManager.shutdown();
        assert !databaseManager.isReady();
    }

    @Test
    void isReady() {
        assert databaseManager.isReady();
    }
}