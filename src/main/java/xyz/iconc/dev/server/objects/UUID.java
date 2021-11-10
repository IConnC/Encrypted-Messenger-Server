package xyz.iconc.dev.server.objects;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;

public class UUID implements Serializable {
    private long epochTime;
    private int randomNumber;
    private ObjectType objectType;

    private long identifier; // 18 digit max


    public UUID(ObjectType _objectType) {
        generateDataset(_objectType);
    }

    public UUID(long _identifier) {
        identifier = _identifier;
    }

    /**
     * Generates all of the values needed to create a unique identifier.
     */
    private void generateDataset(ObjectType _objectType) {
        objectType = _objectType;

        epochTime = Instant.now().getEpochSecond();

        SecureRandom random = new SecureRandom();

        randomNumber = 0;
        for(int i= 0; i< 8 ; i++) {
            // Multiplies the random integer in range 0-9 by 10 to the power of i
            randomNumber += random.nextInt(10) * Math.pow(10,i);
        }

    }

    public long getEpochTime() {
        if (epochTime != 0L) return epochTime;


        return 0L;
    }

    public ObjectType getObjectType() {
        if (objectType != ObjectType.UNDEFINED) return objectType;


        return null;
    }

    /**
     * Converts all values created in the constructor and transforms it into a 64 byte long array.
     *
     * @return long     Returns an 18 digit identifier
     */
    public long getIdentifier() {
        if (identifier != 0L) return identifier;

        

        System.out.println(epochTime);
        System.out.println(randomNumber);
        System.out.println(objectType);

        return 0L;
    }

    public static void main(String[] args) {

        new UUID(ObjectType.ACCOUNT).getIdentifier();

    }
}
