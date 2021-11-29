package xyz.iconc.dev.server.networkObjects;

import java.io.Serializable;

public class Message implements Serializable {
    private final long senderIdentifier;
    private final long channelIdentifier;
    private final long messageIdentifier;
    private final String messageContents;


    /**
     * Used to create a new instance of a message with no associated identifier
     *  yet created.
     *
     * @param _senderIdentifier The message sender's identifier
     * @param _channelIdentifier The identifier of the channel the message was sent in
     * @param _messageContents The encrypted contents of the message
     */
    public Message(long _senderIdentifier, long _channelIdentifier,
                   String _messageContents) {
        senderIdentifier = _senderIdentifier;
        channelIdentifier = _channelIdentifier;
        messageContents = _messageContents;
        messageIdentifier = new UUID(NetworkObjectType.MESSAGE).getIdentifier();
    }


    /**
     * Used to instantiate a message instance that is already active.
     *
     * @param _messageIdentifier The message's identifier
     * @param _senderIdentifier The message sender's identifier
     * @param _channelIdentifier The identifier of the channel the message was sent in
     * @param _messageContents The encrypted contents of the message
     */
    public Message (long _messageIdentifier, long _senderIdentifier,
                    long _channelIdentifier, String _messageContents) {
        senderIdentifier = _senderIdentifier;
        channelIdentifier = _channelIdentifier;
        messageContents = _messageContents;
        messageIdentifier = _messageIdentifier;
    }


    public long getSenderIdentifier() {
        return senderIdentifier;
    }

    public long getChannelIdentifier() {
        return channelIdentifier;
    }

    public long getMessageIdentifier() {
        return messageIdentifier;
    }

    public String getMessageContents() {
        return messageContents;
    }
}
