package xyz.iconc.dev.server;

import xyz.iconc.dev.server.storage.DatabaseManager;

import java.util.Objects;

public class Server {
    public static final int PORT = 28235;
    public static final int THREAD_COUNT = 4;
    public static final long UNIX_EPOCH_MILLISECONDS_START = 1636752382880L; // Epoch time in milliseconds of project start
    private ServerState serverState;
    private static Configuration configuration;
    private final DatabaseManager databaseManager;

    private static Server serverInstance;



    public static void main(String[] args) {
        configuration = new Configuration();
        serverInstance = new Server(PORT, THREAD_COUNT);
    }

    /**
     * Starts the Server Instance and initializes all components of the server.
     *
     * @param  port  the network port that the server listens to for clients.
     * @param  threads the total amount of worker threads used to process clients and messages.
     */
    public Server(int port, int threads) {
        serverState = ServerState.STARTING;

        databaseManager = new DatabaseManager(false);




        serverState = ServerState.RUNNING;
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
            return null;
        }
        ServerState currentServerState = serverInstance.serverState;

        if (currentServerState == ServerState.RUNNING) {
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
