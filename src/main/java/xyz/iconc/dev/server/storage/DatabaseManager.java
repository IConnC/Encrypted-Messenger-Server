package xyz.iconc.dev.server.storage;

import xyz.iconc.dev.server.Configuration;
import xyz.iconc.dev.server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private String connectionUrl;
    private Connection connection;

    public DatabaseManager(boolean debug) {
        if (!debug) {
            connectionUrl = Server.getConfig().getConfigValue(Configuration.ConfigOptions.DATABASE_CONNECTION_STRING);
        } else {
            connectionUrl = new Configuration().getConfigValue(Configuration.ConfigOptions.DATABASE_CONNECTION_STRING);
        }


        initializeDatabaseConnection();
    }


    private void initializeDatabaseConnection() {
        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager(true);

    }
}
