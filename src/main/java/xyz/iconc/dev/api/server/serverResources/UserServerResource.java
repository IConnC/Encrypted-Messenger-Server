package xyz.iconc.dev.api.server.serverResources;

import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.resources.UserResource;

public class UserServerResource extends ServerResourceAbstract implements UserResource {

    private static volatile User user;

    @Override
    public void doInit() {
        super.doInit();
        user = new User(identifier);
    }

    @Override
    public User retrieve() {
        return user;
    }

    @Override
    public void store(User _user) {
        user = _user;
    }

    @Override
    public void remove() {
        user = null;
    }
}
