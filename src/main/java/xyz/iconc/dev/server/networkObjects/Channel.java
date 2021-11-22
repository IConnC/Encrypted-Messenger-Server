package xyz.iconc.dev.server.networkObjects;

import java.io.Serializable;
import java.time.Instant;


public class Channel implements Serializable {
    public Channel() {

    }

    public static void main(String[] args) {
        long ut1 = Instant.now().getEpochSecond();
        System.out.println(ut1/1000);

    }
}
