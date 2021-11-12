package xyz.iconc.dev.server.objects;

import xyz.iconc.dev.server.Server;
import xyz.iconc.dev.server.Utility;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;

public class UUID implements Serializable {
    private static final int RANDOM_NUMBER_MAX_LENGTH = 8;
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
        for(int i= 0; i< RANDOM_NUMBER_MAX_LENGTH ; i++) {
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
        //[] bytes = new byte[53];
        //bytes[0] = 4;
        //System.out.println(bytes[0]);
        BitSet bitSet = new BitSet(63);

        bitSet.set(62, true);
        bitSet.set(61, true);
        //System.out.println(Arrays.toString(bitSet.toByteArray()));


        long identifierTest = 0L;

        identifierTest += epochTime;
        identifierTest = identifierTest << 19;
        System.out.println(identifierTest);

        identifierTest +=  randomNumber;
        System.out.println(identifierTest);


        //System.out.println(Long.BYTES);


        //System.out.println(Arrays.toString(Utility.getBinary(identifier)));






        System.out.println();
        System.out.println(epochTime);
        System.out.println(randomNumber);
        System.out.println(objectType.getTypeId());

        return 0L;
    }


    public static void main(String[] args) {

        new UUID(ObjectType.ACCOUNT).getIdentifier();

    }
}
