package xyz.iconc.dev.server.networkObjects;

import java.io.*;
import java.time.Instant;


public class Channel implements Serializable {
    private long channelIdentifier;
    private long creationEpoch;

    /**
     * Only use if Channel is already created.
     *
     * @param _identifier UUID identifier object
     * @param _timestamp UUID timestamp of creation
     */
    public Channel(long _identifier, long _timestamp) {
        channelIdentifier = _identifier;
        creationEpoch = _timestamp;
    }

    /**
     * Use only if Channel doesn't already exist.
     * Preferred to use CreateChannel method
     */
    public Channel() {
        Channel channel = CreateChannel();
        channelIdentifier = channel.getChannelIdentifier();
        creationEpoch = channel.getCreationEpoch();
    }



    public long getChannelIdentifier() {
        return channelIdentifier;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }


    public static Channel CreateChannel() {
        UUID uuid = new UUID(NetworkObjectType.CHANNEL);
        return new Channel(uuid.getIdentifier(), uuid.getEpochTime());
    }





    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Channel channel = new Channel();
        FileOutputStream file = new FileOutputStream
                ("./test.txt");

        ObjectOutputStream out = new ObjectOutputStream
                (file);

        // Method for serialization of object
        out.writeObject(channel);

        out.close();
        file.close();

        System.out.println("Object has been serialized\n"
                + "Data before Deserialization.");
        System.out.println(channel);


        FileInputStream in = new FileInputStream("./test.txt");
        ObjectInputStream stream = new ObjectInputStream(in);
        Channel channel1 = (Channel)stream.readObject();

        System.out.println(channel.getCreationEpoch());
        System.out.println(channel.getChannelIdentifier());
        System.out.println(channel1.getCreationEpoch());
        System.out.println(channel1.getChannelIdentifier());

        new File("./test.txt").delete();
    }
}
