package xyz.iconc.dev.server.objects;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;

public class UUID implements Serializable {
    public long epochTime;
    public long randomNumber;

    public UUID() {
        SecureRandom random = new SecureRandom();
        epochTime = Instant.now().getEpochSecond();

        randomNumber = random.nextInt(16);


        System.out.println(randomNumber);

    }

    public static void main(String[] args) {
        new UUID();
    }
}
