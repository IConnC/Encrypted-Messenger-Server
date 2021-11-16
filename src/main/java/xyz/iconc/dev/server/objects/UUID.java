package xyz.iconc.dev.server.objects;

import javafx.scene.paint.RadialGradient;
import xyz.iconc.dev.server.Server;
import xyz.iconc.dev.server.Utility;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;

public class UUID implements Serializable {
    private static final int RANDOM_NUMBER_MAX_LENGTH = 5;
    private static final int RANDOM_NUMBER_TOP_BOUND = 5; // First number in the maximum number
    private long epochTime; // 42 Bits max (Including 0)
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

        // Starts epoch at project start date.
        epochTime = Instant.now().toEpochMilli() - Server.UNIX_EPOCH_MILLISECONDS_START;

        SecureRandom random = new SecureRandom();


        randomNumber = 0;
        int bound = 10;

        for(int i=0; i< RANDOM_NUMBER_MAX_LENGTH ; i++) {
            if (i == RANDOM_NUMBER_MAX_LENGTH-1) bound = RANDOM_NUMBER_TOP_BOUND;
            // Multiplies the random integer in range 0-9 by 10 to the power of i
            randomNumber += random.nextInt(bound) * Math.pow(10,i);
        }
    }

    public long getEpochTime() {
        if (epochTime != 0L) return epochTime;

        epochTime = identifier >> 19;

        return epochTime;
    }

    private long getRandomNumber() {
        if (randomNumber != 0L) return randomNumber;

        randomNumber = (int) (identifier - getEpochTime()) >> 3;

        return randomNumber;

    }

    public ObjectType getObjectType() {
        if (objectType != ObjectType.UNDEFINED) return objectType;
        objectType = ObjectType.fromInteger((int)(identifier - getRandomNumber()) >> 15);

        return objectType;
    }

    /**
     * Converts all values created in the constructor and transforms it into a 64 byte long array.
     *
     * 53 bits maximum size for long
     * 400000000000000000
     *
     * Timestamp uses 44 bits from 63-19
     * Random Number uses 16 bits from 3-18 ranging from values of 0-65536
     * Object Type uses 2 bits from 2-0 ranging from values 0-3
     *
     *
     *
     * @return long     Returns an 18 digit identifier
     */
    public long getIdentifier() {
        //11111111111111111111111111111111111111111111111111111111111111111
        if (identifier != 0L) return identifier;

        long finalIdentifier;
        long tempNumber;

        tempNumber = epochTime << 19; // Offsets epochTime 19 bits left
        finalIdentifier = tempNumber;

        tempNumber = randomNumber << 3; // Offsets random number 3 bits left

        finalIdentifier += tempNumber;

        tempNumber = objectType.getTypeId() - 1; // Doesn't offset
        finalIdentifier += tempNumber;

        return finalIdentifier;
    }


    public static void main(String[] args) {

        UUID t = new UUID(ObjectType.ACCOUNT);
        System.out.println(t.getIdentifier());
        System.out.println(t.getEpochTime());
        System.out.println(t.getObjectType());
        System.out.println();


    }
}