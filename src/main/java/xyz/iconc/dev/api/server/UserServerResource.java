package xyz.iconc.dev.api.server;

import org.restlet.resource.ServerResource;
import xyz.iconc.dev.api.shared.objects.User;
import xyz.iconc.dev.api.shared.resources.UserResource;

public class UserServerResource extends ServerResource implements UserResource {

    private static volatile User user = new User("TestUsername");

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
