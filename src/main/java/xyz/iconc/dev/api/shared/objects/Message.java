package xyz.iconc.dev.api.shared.objects;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Message implements Serializable {

    private final long identifier;
    private volatile String messageContents;

    public Message(long identifier) {
        this.identifier = identifier;
        messageContents = "";
    }

    public long getIdentifier() {
        return identifier;
    }

    public String getMessageContents() {
        if (messageContents.length() == 0) {
            messageContents = Base64.getEncoder().encodeToString("TEST MESSAGE 123".getBytes(StandardCharsets.UTF_8));
        }

        return messageContents;
    }

}
