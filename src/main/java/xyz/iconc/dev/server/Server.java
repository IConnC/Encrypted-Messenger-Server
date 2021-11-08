package xyz.iconc.dev.server;

public class Server {
    public static final int PORT = 28235;
    public static final int THREAD_COUNT = 4;
    private ServerState serverState;

    private static Server serverInstance;



    public static void main(String[] args) {
        serverInstance = new Server(PORT, THREAD_COUNT);
    }

    public Server(int port, int threads) {
        serverState = ServerState.STARTING;





        serverState = ServerState.RUNNING;
    }

    public void TerminateServer() {
        serverState = ServerState.STOPPING;



        serverState = ServerState.STOPPED;
    }



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


    public static ServerState getServerState() {
        if (serverInstance == null) return ServerState.STOPPED;
        return serverInstance.serverState;
    }

    public enum ServerState {
        STOPPED,
        STOPPING,
        STARTING,
        RUNNING
    }
}
