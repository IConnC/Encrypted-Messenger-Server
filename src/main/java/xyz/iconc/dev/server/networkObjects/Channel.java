package xyz.iconc.dev.server.networkObjects;

import java.io.Serializable;
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
    public static void main(String[] args) {

    }
}
