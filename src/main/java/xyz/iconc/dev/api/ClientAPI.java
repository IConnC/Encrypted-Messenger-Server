package xyz.iconc.dev.api;

import org.restlet.Connector;
import org.restlet.resource.ClientResource;

import java.io.IOException;

public class ClientAPI {
    public static void main(String[] args) throws IOException {
        new ClientResource("http://restlet.com").get().write(System.out);
    }
}
