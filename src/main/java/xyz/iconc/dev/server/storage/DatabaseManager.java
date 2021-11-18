package xyz.iconc.dev.server.storage;

import xyz.iconc.dev.server.Configuration;
import xyz.iconc.dev.server.Server;
import xyz.iconc.dev.server.objects.IReady;
import xyz.iconc.dev.server.objects.StartupObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class DatabaseManager extends StartupObject implements Runnable {
    private AtomicBoolean readyState;
    private final String connectionUrl;
    private Connection connection;

    public DatabaseManager(boolean debug) {
        super();
        readyState = new AtomicBoolean(false);

        if (!debug) {
            connectionUrl = Server.getConfig().getConfigValue(Configuration.ConfigOptions.DATABASE_CONNECTION_STRING);
        } else {
            connectionUrl = new Configuration().getConfigValue(Configuration.ConfigOptions.DATABASE_CONNECTION_STRING);
        }
    }

    @Override
    public void run() {
        initializeDatabaseConnection();
        readyState.set(true);

        latchListLock.lock();
        for (CountDownLatch latch : latchList) {
            latch.countDown();
        }
        latchListLock.unlock();
    }

    private void initializeDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean connectionCheck() {
        if (connection == null) return false;
        return readyState.get();
    }





    public void shutdown() {

    }


    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager(true);

    }

    @Override
    public boolean isReady() {
        return readyState.get();
    }

}
