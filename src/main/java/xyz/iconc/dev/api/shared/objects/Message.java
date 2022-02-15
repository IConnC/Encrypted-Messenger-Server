package xyz.iconc.dev.api.shared.objects;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Message implements Serializable {

    private volatile long identifier;
    private volatile byte[] messageContents;

    public Message(long identifier) {
        this.identifier = identifier;
    }

    public long getIdentifier() {
        return identifier;
    }

    public byte[] getMessageContents() {
        if (messageContents.length == 0) {
            messageContents = Base64.getEncoder().encode( "TEST MESSAGE 123".getBytes(StandardCharsets.UTF_8));
        }

        return messageContents;
    }

}
