package xyz.iconc.dev.api.shared.objects;

import java.io.Serializable;

public class User implements Serializable {
    private volatile long identifier;
    private volatile String username;

    public User(long identifier) {
        this.identifier = identifier;
        this.username = "";
    }

    public String getUsername() {
        if (username.isEmpty()) {
            username = "TESTUSERNAME";
        }

        return username;
    }

    public long getIdentifier() {
        return identifier;
    }
}
