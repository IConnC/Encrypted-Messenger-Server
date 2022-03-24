package xyz.iconc.dev.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.objects.Message;
import xyz.iconc.dev.objects.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceManager {
    private final Logger logger = LoggerFactory.getLogger(ResourceManager.class);
    private final DatabaseManager databaseManager;

    ExecutorService workerThreads = Executors.newFixedThreadPool(Server.THREAD_COUNT);

    private CopyOnWriteArrayList<Channel> channels;


    public ResourceManager() {
        databaseManager = Server.getServerInstance().getDatabaseManager();
        if (databaseManager == null) {
            logger.error("Database not initialized!");
            System.exit(1);
        }
        channels = new CopyOnWriteArrayList<>();
    }

    public void start() {
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

    private void initializeChannels() {
        channels.clear();
        // Gets all channels from database and populates all of their data
        AtomicInteger runningPopulations = new AtomicInteger(0);
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
    }


    /**
     *
     * @param channel Channel to find in the in-memory store
     * @return Proper channel object
     */
    private Channel getChannel(Channel channel) {
        return getChannel(channel.getChannelIdentifier());
    }

    /**
     *
     * @param channelIdentifier Channel identifier to find in the in-memory store
     * @return Proper channel object
     */
    private Channel getChannel(long channelIdentifier) {
        return null;
    }

    public void addMessage(Message message, Channel channel) {
        workerThreads.submit(new Runnable() {
            @Override
            public void run() {
                databaseManager.insert_message(message);
            }
        });

    }

    public static void main(String[] args) {

    }
}
