package xyz.iconc.dev.objects;

import xyz.iconc.dev.server.Server;

import java.io.Serializable;

public class User implements Serializable {
    private final long userIdentifier;
    private String username;
    protected String hashedPassword;

    private long dateRegistered;

    private Channel[] subscribedChannels;
    private long lastMessageReceivedEpoch;

    private boolean isPopulated;


    public User(long userIdentifier) {
        this.userIdentifier = userIdentifier;
        isPopulated = false;
    }


    public User(long userIdentifier, String username, String hashedPassword, long dateRegistered,
                long lastMessageReceived, Channel[] subscribedChannels) {
        this.userIdentifier = userIdentifier;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.dateRegistered = dateRegistered;
        this.lastMessageReceivedEpoch = lastMessageReceived;
        this.subscribedChannels = subscribedChannels;
        isPopulated = true;
    }

    public void populateData() {
        User user = Server.getServerInstance().getDatabaseManager().get_account(userIdentifier);
        this.username = user.getUsername();
        this.hashedPassword = user.getHashedPassword();
        this.dateRegistered = user.getDateRegistered();
        this.lastMessageReceivedEpoch = user.getLastMessageReceivedEpoch();
        this.subscribedChannels = user.getSubscribedChannels();

        isPopulated = true;
    }

    public void addChannelSubscription(Channel newChannel){
        Channel[] channels = new Channel[subscribedChannels.length + 1];
        for (int i=0; i<subscribedChannels.length; i++) {
            channels[i] = subscribedChannels[i];
        }
        channels[subscribedChannels.length + 1] = newChannel;
    }

    public long getUserIdentifier() {
        return userIdentifier;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return "hashedPassword";
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public long getLastMessageReceivedEpoch() {
        return lastMessageReceivedEpoch;
    }

    public void setLastMessageReceivedEpoch(long epoch) {
        lastMessageReceivedEpoch = epoch;
    }

    public boolean isPopulated() {
        return isPopulated;
    }

    public Channel[] getSubscribedChannels() {
        return subscribedChannels;
    }

    public static void main(String[] args) {

    }
}

