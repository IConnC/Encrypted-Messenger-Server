package xyz.iconc.dev.objects;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Used for client posting messages to the server.
 *
 */

public class PostMessage implements Serializable {

    private volatile String messageContents;
    private volatile long channelIdentifier;


    public PostMessage() {
    }

    public PostMessage(String contents, long channelId) {
        messageContents = contents;
        channelIdentifier = channelId;
    }

    public long getChannelIdentifier() {
        return channelIdentifier;
    }

    public String getMessageContents() {
        if (messageContents.length() == 0) {
            messageContents = Base64.getEncoder().encodeToString("TEST MESSAGE 123".getBytes(StandardCharsets.UTF_8));
        }

        return messageContents;
    }

}
