package xyz.iconc.dev.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLiteJDBC {
    private static final String ACCOUNT_INFORMATION_TABLE_CREATION_SQL = "create table client_details " +
            "(identifier bigint(19) not null constraint client_details_pk primary key, password   text(256)  " +
            "not null); create unique index client_details_identifier_uindex on client_details (identifier);";
    private static final String CHANNEL_TABLE_CREATION_SQL = "";
    private static final String MESSAGE_TABLE_CREATION_SQL = "";



    Logger logger = LoggerFactory.getLogger(SQLiteJDBC.class);
    Connection connection;

    public SQLiteJDBC() {
        Map<String, String> creationMap = new HashMap<>();

        creationMap.put("client_details", ACCOUNT_INFORMATION_TABLE_CREATION_SQL);

        logger.debug("Opening database...");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:client.db");
            logger.debug("Database successfully opened.");

        } catch (Exception e) {
            logger.error("Error opening database...\n" + e);
            System.exit(1);
        }


        logger.debug("Verifying database integrity...");
        try {

            PreparedStatement stmt;
            for (String key : creationMap.keySet()) {
                stmt = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name='" + key + "';");
                stmt.execute();
                if (!stmt.getResultSet().next()) {
                    connection.createStatement().execute(creationMap.get(key));
                }
                stmt.close();
            }

        } catch (Exception e) {
            logger.error("Error verifying database integrity...\n" + e.toString());
            System.exit(1);
        }
        logger.debug("Successfully validated database integrity!");
    }

    public void addClientDetails(long identifier, String password) {
        String sql = "INSERT INTO client_details VALUES (?,?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, identifier);
            stmt.setString(2, password);

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            logger.error(e.toString());
            System.exit(1);
        }
    }

    /**
     * Returns client details such as username and password
     * @return String array with a client identifier at index 0 and a password at index 1
     */
    public String[] getClientDetails() {
        String[] output = new String[2];
        String sql = "SELECT * FROM client_details LIMIT 1";


        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            ResultSet rs = stmt.getResultSet();

            if (!rs.next()) return null;

            output[0] = rs.getString(1);
            output[1] = rs.getString(2);

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logger.error(e.toString());
            System.exit(1);
        }
        return output;
    }



    public static void main(String[] args) {
        SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
    }
}
