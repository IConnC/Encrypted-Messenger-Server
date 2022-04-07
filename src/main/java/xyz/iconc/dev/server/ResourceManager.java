package xyz.iconc.dev.server;

import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.objects.Message;
import xyz.iconc.dev.objects.Channel;
import xyz.iconc.dev.objects.UUID;
import xyz.iconc.dev.objects.User;
import xyz.iconc.dev.server.utilities.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceManager {
    private final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    private final DatabaseManager databaseManager;

    ExecutorService workerThreads;

    private final CopyOnWriteArrayList<Channel> channels;
    private final CopyOnWriteArrayList<User> users;


    public ResourceManager() {
        databaseManager = Server.getServerInstance().getDatabaseManager();
        if (databaseManager == null) {
            logger.error("Database not initialized!");
            System.exit(1);
        }

        workerThreads = Server.getServerInstance().getWorkerThreads();
        channels = new CopyOnWriteArrayList<>();
        users = new CopyOnWriteArrayList<>();
    }

    public void start() {
        initializeUsers();
        initializeChannels();

        /*
        System.out.println(channels);
        for (Channel channel : channels) {
            System.out.println("\n\n" + channel.getChannelName());
            for (Message message : channel.getMessages()) {
                System.out.println(message);
            }
        }
        */

    }

    private void initializeUsers() {

        logger.info("Initializing in-memory users...");
        users.clear();

        AtomicInteger runningPopulations = new AtomicInteger(0);

        // Gets all channels from database and populates all of their data
        for (User user : databaseManager.get_accounts()) {
            runningPopulations.set(runningPopulations.intValue() + 1); // Adds a thread as working
            workerThreads.submit(() -> {
                user.populateData();
                runningPopulations.set(runningPopulations.intValue() - 1); // Signals thread is finished
            });

            users.add(user);
        }
        // Ensures all channel data is populated before proceeding
        while (runningPopulations.intValue() != 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.toString());
                System.exit(1);
            }
        }

        logger.info("Successfully initialized in-memory users!");
    }

    private void initializeChannels() {
        AtomicInteger runningPopulations = new AtomicInteger(0);

        logger.info("Initializing in-memory channels...");
        channels.clear();
        // Gets all channels from database and populates all of their data
        for (Channel channel : databaseManager.get_channels()) {
            runningPopulations.set(runningPopulations.intValue() + 1); // Adds a thread as working
            workerThreads.submit(new Runnable() {
                @Override
                public void run() {
                    channel.populateData();
                    runningPopulations.set(runningPopulations.intValue() - 1); // Signals thread is finished
                }
            });

            channels.add(channel);
        }
        // Ensures all channel data is populated before proceeding
        while (runningPopulations.intValue() != 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.toString());
                System.exit(1);
            }
        }
        logger.info("Successfully loaded all channels into memory!");
    }

    public Message[] pollUser(long identifier) {
        User user = getUser(identifier);

        List<Message> messages = new ArrayList<>();

        for (Channel channel : user.getSubscribedChannels()) {
            messages.addAll(getMessagesFromEpoch(channel.getChannelIdentifier(), user.getLastMessageReceivedEpoch()));
        }
        long epoch = Utility.GetUnixEpoch();
        user.setLastMessageReceivedEpoch(epoch);

        workerThreads.submit(() -> {
            databaseManager.update_account_last_message_received_epoch(
                    user.getUserIdentifier(), epoch);
        });


        Message[] messagesArray = new Message[messages.size()];
        for (int i=0; i < messagesArray.length; i++) {
            messagesArray[i] = messages.get(i);
        }


        return messagesArray;
    }

    public User getUser(long identifier) {
        for (User user : users) {
            if (user.getUserIdentifier() == identifier) return user;
        }
        return null;
    }

    public List<Message> getMessagesFromEpoch(long channelIdentifier, long epoch) {
        List<Message> messages = new ArrayList<>();

        for (Channel channel : channels) {
            if (channel.getChannelIdentifier() == channelIdentifier) {
                for (Message message : channel.getMessages()) {
                    if (new UUID(message.getMessageIdentifier()).getEpochTime() >= epoch) {
                        messages.add(message);
                    }
                }
                break;
            }
        }

        return messages;
    }



    public Message getMessage(long identifier, long channelIdentifier) {
        for (Channel _channel : channels) {
            if (_channel.getChannelIdentifier() == channelIdentifier) {
                for (Message message : _channel.getMessages()) {
                    if (message.getMessageIdentifier() == identifier) return message;
                }
                break;
            }
        }
        return null;
    }

    public Message getMessage(long identifier) {
        for (Channel _channel : channels) {
            for (Message message : _channel.getMessages()) {
                if (message.getMessageIdentifier() == identifier) return message;
            }
        }
        return null;
    }



    /**
     *
     * @param channel Channel to find in the in-memory store
     * @return Proper channel object
     */
    public Channel getChannel(Channel channel) {
        return getChannel(channel.getChannelIdentifier());
    }

    /**
     *
     * @param channelIdentifier Channel identifier to find in the in-memory store
     * @return Proper channel object
     */
    public Channel getChannel(long channelIdentifier) {
        for (Channel channel : channels) {
            if (channel.getChannelIdentifier() == channelIdentifier) return channel;
        }
        return null;
    }

    public void addMessage(Message message) {
        getChannel(message.getChannelIdentifier()).addMessage(message);
        workerThreads.submit(() -> {
            databaseManager.insert_message(message);
        });

    }




    public static void main(String[] args) {

    }
}
