package xyz.iconc.dev.api.shared.objects;

import xyz.iconc.dev.objects.Message;

import java.io.Serializable;
import java.util.List;

public class PollObject implements Serializable {
    private Message[] newMessages;

    public PollObject(long userIdentifier) {

    }
}
