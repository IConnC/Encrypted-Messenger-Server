package xyz.iconc.dev.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.api.ServerAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final int PORT = 28235;
    public static final int THREAD_COUNT = 4;
    public static final long UNIX_EPOCH_MILLISECONDS_START = 1636752382880L; // Epoch time in milliseconds of project start

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerState serverState;
    private static Configuration configuration;
    private final DatabaseManager databaseManager;
    private ResourceManager resourceManager;

    private final ExecutorService workerThreads = Executors.newFixedThreadPool(Server.THREAD_COUNT);

    private static Server serverInstance;


    public static void main(String[] args) {
        configuration = new Configuration();
        serverInstance = new Server(PORT, THREAD_COUNT);
        serverInstance.run();
    }

    /**
     * Starts the Server Instance and initializes all components of the server.
     *
     * @param  port  the network port that the server listens to for clients.
     * @param  threads the total amount of worker threads used to process clients and messages.
     */
    public Server(int port, int threads) {
        serverState = ServerState.STARTING;

        if (configuration == null) configuration = new Configuration();

        databaseManager = new DatabaseManager(false);
    }

    public void run() {
        serverState = ServerState.STARTING;

        resourceManager = new ResourceManager();

        resourceManager.start();

        ServerAPI serverAPI = new ServerAPI(resourceManager);

        workerThreads.submit(new Runnable() {
            @Override
            public void run() {
                serverAPI.Start();
            }
        });

        serverState = ServerState.RUNNING;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");
            serverState = ServerState.STOPPING;

            logger.info("Shutting down worker threads...");
            workerThreads.shutdown();
            logger.info("Done!");

            logger.info("Shutdown Complete!");
        }));
        while (serverState == ServerState.RUNNING) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error(e.toString());
            }
        }
        terminateServer();
    }

    /**
     *  Starts the shutdown process for the server instance.
     *  <p>
     *  Sets the serverState variable as STOPPING when run and sets the state as STOPPED
     *  when completed.
     */
    public void terminateServer() {
        serverState = ServerState.STOPPING;


        databaseManager.shutdown();

        serverState = ServerState.STOPPED;
    }




    public void registerManagers() {

    }



    /**
     *  Gets the server state according to the following conditions.
     *    - If the serverInstance is null, it instantiates a new server instance and returns null.
     *    - If the serverState is anything other than RUNNING, returns null.
     *    - If the serverState is RUNNING, returns current serverInstance.
     *  <p>
     *  The method caller must expect a null value and check serverState variable for further instructions.
     *
     * @return      the server instance or null
     */
    public static Server getServerInstance() {
        if (serverInstance == null) {
            serverInstance = new Server(PORT, THREAD_COUNT);
            return getServerInstance();
        }
        ServerState currentServerState = serverInstance.serverState;

        if (currentServerState == ServerState.STARTING || currentServerState == ServerState.RUNNING) {
            return serverInstance;
        }

        return null;
    }


    /**
     *  Gets the current ServerState in the instance variable serverState.
     *
     * @return      the current ServerState
     */
    public static ServerState getServerState() {
        if (serverInstance == null) return ServerState.STOPPED;
        return serverInstance.serverState;
    }

    public static Configuration getConfig() {
        return configuration;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ExecutorService getWorkerThreads() {
        return workerThreads;
    }

    /**
     *  The current state that the server is in.
     *  ServerState.STOPPED, any objects that have knowledge of this state should self destruct.
     *  ServerState.STOPPING, any objects that have knowledge of this state should start shutting down.
     *  ServerState.STARTING, any objects that have knowledge of this state should wait until ServerState is RUNNING.
     *  ServerState.RUNNING, signifies that the server is fully operational
     */
    public enum ServerState {
        STOPPED,
        STOPPING,
        STARTING,
        RUNNING
    }
}
