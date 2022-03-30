package xyz.iconc.dev.objects.safe;

import xyz.iconc.dev.objects.Channel;
import xyz.iconc.dev.objects.User;

public class SafeUser extends User {
    public SafeUser(long userIdentifier) {
        super(userIdentifier);
        this.populateData();
    }

    public static SafeUser GenerateSafeUser(User user) {
        return new SafeUser(user.getUserIdentifier());
    }

}
