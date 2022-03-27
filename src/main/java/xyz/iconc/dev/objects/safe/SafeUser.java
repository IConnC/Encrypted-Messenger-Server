package xyz.iconc.dev.objects.safe;

import xyz.iconc.dev.objects.User;

public class SafeUser extends User {
    public SafeUser(long _identifier, String _username, long _dateRegistered, int _accountType) {
        super(_identifier, _username, "", "", _dateRegistered, _accountType);
    }

    public static SafeUser GenerateSafeUser(User user) {
        return new SafeUser(user.getUserIdentifier(), user.getUsername(),
                user.getDateRegistered(), user.getAccountType());
    }

}
