package xyz.iconc.dev.server.storage;

import xyz.iconc.dev.server.objects.StartupObject;

import java.util.concurrent.CountDownLatch;

public class NetworkDataManager extends StartupObject implements Runnable {
    private final DatabaseManager databaseManager;

    public NetworkDataManager(DatabaseManager _databaseManager) {
        super();
        databaseManager = _databaseManager;

    }


    public void t () {

    }





    @Override
    public boolean isReady() {
        return readyState.get();
    }

    @Override
    public void run() {
        // Ensures that databaseManager is ready to be used
        if (!databaseManager.isReady()) {
            CountDownLatch cl = new CountDownLatch(1);
            databaseManager.registerLatch(cl);

            try {
                cl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else readyState.set(true);
    }
}