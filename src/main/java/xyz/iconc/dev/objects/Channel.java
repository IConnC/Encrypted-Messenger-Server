package xyz.iconc.dev.objects;

import xyz.iconc.dev.server.DatabaseManager;
import xyz.iconc.dev.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Channel implements Serializable {
    private final long channelIdentifier;
    private String channelName;
    private final long creationEpoch;
    private CopyOnWriteArrayList<Message> messages;
    private CopyOnWriteArrayList<User> subscribedUsers;

    /**
     * Only use if Channel is already created.
     *
     * @param _identifier UUID identifier object
     * @param _timestamp UUID timestamp of creation
     */
    public Channel(long _identifier, String _channelName, long _timestamp) {
        channelIdentifier = _identifier;
        channelName = _channelName;
        creationEpoch = _timestamp;

        messages = new CopyOnWriteArrayList<>();
        subscribedUsers = new CopyOnWriteArrayList<>();
    }

    public void populateData() {
        DatabaseManager db = Server.getServerInstance().getDatabaseManager();
        messages.addAll(db.get_messages(channelIdentifier));
        subscribedUsers.addAll(db.get_channelMembers(channelIdentifier));
    }

    /**
     * Use only if Channel doesn't already exist.
     * Preferred to use CreateChannel method
     */
    public Channel(String _channelName) {
        Channel channel = CreateChannel(_channelName);
        channelIdentifier = channel.getChannelIdentifier();
        channelName = _channelName;
        creationEpoch = channel.getCreationEpoch();
    }



    public long getChannelIdentifier() {
        return channelIdentifier;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String _channelName) {
        channelName = _channelName;
    }


    public static Channel CreateChannel(String channelName) {
        UUID uuid = new UUID(NetworkObjectType.CHANNEL);
        return new Channel(uuid.getIdentifier(), channelName, uuid.getEpochTime());
    }



    public boolean equals(Channel channel) {
        return channel.getChannelIdentifier() == this.channelIdentifier;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public List<User> getMembers() {
        return new ArrayList<>(subscribedUsers);
    }


    public void removeSubscribedMember(long memberIdentifier) {

    }

    public void removeMessage(long messageIdentifier) {}



    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Channel channel = new Channel("");
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
