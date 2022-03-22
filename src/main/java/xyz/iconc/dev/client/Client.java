package xyz.iconc.dev.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.iconc.dev.api.ClientAPI;

import java.io.IOException;
import java.util.Scanner;

public class Client {

    //Test User Details - 5887016195674298 - aDifferentPassword
    private static long identifier;

    private ClientAPI clientAPI;

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private static Client client;

    private boolean exit;

    public Client() {
        client = this;
        exit = false;
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);

        logger.info("Starting Client...");
        System.out.println("Please input client identifier:");
        long identifier = Long.parseLong(scanner.nextLine());

        System.out.println("Please input password:");
        String password = scanner.nextLine();

        ClientAPI clientAPI = new ClientAPI(identifier, password);


        System.out.println("");

        while (!exit) {

        }
    }

    public static void setClient(Client _client) {
        client = _client;
    }

    public static Client getClient() {
        return client;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();

        System.out.println();
    }
}
