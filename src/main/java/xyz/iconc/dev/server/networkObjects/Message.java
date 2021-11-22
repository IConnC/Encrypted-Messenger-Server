package xyz.iconc.dev.server.networkObjects;

import java.io.Serializable;

public class Message implements Serializable {
    public String senderIdentifier;
    public long channelIdentifier;
    public byte[] messageContents;

    public Message() {

    }
}
